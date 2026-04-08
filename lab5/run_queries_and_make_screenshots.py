#!/usr/bin/env python3
"""
Execute the required Lab5a queries and generate PNG 'screenshots'
showing (1) the SQL and (2) the output.

Run from lab5/: .venv/bin/python run_queries_and_make_screenshots.py
"""

from __future__ import annotations

import sqlite3
from pathlib import Path

from PIL import Image, ImageDraw, ImageFont

HERE = Path(__file__).resolve().parent
DB = HERE / "ads-lab5a.sqlite"
SQL_SCRIPT = HERE / "myADSDentalSurgeryDBScript.sql"
OUT_DIR = HERE / "query-screenshots"


def load_font(size: int) -> ImageFont.FreeTypeFont | ImageFont.ImageFont:
    for name in (
        "/System/Library/Fonts/Menlo.ttc",
        "/System/Library/Fonts/Monaco.dfont",
        "/usr/share/fonts/truetype/dejavu/DejaVuSansMono.ttf",
    ):
        p = Path(name)
        if p.exists():
            try:
                return ImageFont.truetype(str(p), size=size)
            except OSError:
                continue
    return ImageFont.load_default()


def render_text_png(title: str, sql: str, output: str, out_path: Path) -> None:
    font = load_font(14)
    font_title = load_font(18)
    pad = 24
    line_h = int(font.size * 1.35)

    def lines(block: str) -> list[str]:
        return block.rstrip().splitlines() if block.strip() else ["(no output)"]

    sql_lines = lines(sql)
    out_lines = lines(output)

    # Crop to a reasonable max while still looking like a screenshot
    max_lines = 45
    if len(sql_lines) > 18:
        sql_lines = sql_lines[:18] + ["-- ... (truncated for screenshot)"]
    if len(out_lines) > max_lines:
        out_lines = out_lines[:max_lines] + ["... (truncated)"]

    content_lines = (
        [f"{title}", ""]
        + ["-- SQL"] + sql_lines
        + ["", "-- Output"] + out_lines
    )

    # Measure width
    tmp = Image.new("RGB", (10, 10))
    dtmp = ImageDraw.Draw(tmp)
    max_w = 0
    for ln in content_lines:
        w = int(dtmp.textlength(ln, font=font))
        max_w = max(max_w, w)

    width = max(1100, max_w + pad * 2)
    height = pad * 2 + 44 + line_h * len(content_lines)

    img = Image.new("RGB", (width, height), (250, 250, 252))
    draw = ImageDraw.Draw(img)

    # Header bar
    draw.rectangle([0, 0, width, 54], fill=(35, 35, 45))
    draw.text((pad, 14), title, fill=(255, 255, 255), font=font_title)

    # Body area
    y = 70
    for ln in content_lines[1:]:
        color = (30, 30, 30)
        if ln.startswith("-- "):
            color = (10, 50, 110)
        draw.text((pad, y), ln, fill=color, font=font)
        y += line_h

    # Frame
    draw.rounded_rectangle([8, 8, width - 8, height - 8], radius=16, outline=(120, 120, 130), width=2)
    img.save(out_path, format="PNG", optimize=True)


def table_output(cur: sqlite3.Cursor) -> str:
    rows = cur.fetchall()
    headers = [d[0] for d in cur.description] if cur.description else []
    # basic column formatting
    cols = len(headers)
    widths = [len(h) for h in headers]
    for r in rows:
        for i in range(cols):
            widths[i] = max(widths[i], len("" if r[i] is None else str(r[i])))

    def fmt_row(vals: list[str]) -> str:
        return " | ".join(v.ljust(widths[i]) for i, v in enumerate(vals))

    out = []
    if headers:
        out.append(fmt_row(headers))
        out.append("-+-".join("-" * w for w in widths))
    for r in rows:
        out.append(fmt_row([("" if v is None else str(v)) for v in r]))
    return "\n".join(out) if out else "(no rows)"


def main() -> None:
    OUT_DIR.mkdir(exist_ok=True)

    # Build DB from script (schema + data)
    sql_all = SQL_SCRIPT.read_text(encoding="utf-8")
    con = sqlite3.connect(DB)
    try:
        con.executescript(sql_all)
        con.commit()
    finally:
        con.close()

    con = sqlite3.connect(DB)
    try:
        cur = con.cursor()

        queries = [
            (
                "Q1 — All dentists (sorted by last name)",
                """SELECT dentist_id, first_name, last_name, contact_phone_number, email, specialization
FROM dentists
ORDER BY last_name ASC, first_name ASC;""",
            ),
            (
                "Q2 — Appointments for dentist_id = 101 (include patient info)",
                """SELECT
  a.appointment_id,
  a.start_at,
  a.status,
  a.channel,
  p.patient_id,
  p.first_name  AS patient_first_name,
  p.last_name   AS patient_last_name,
  p.contact_phone_number AS patient_phone,
  p.email       AS patient_email
FROM appointments a
JOIN patients p ON p.patient_id = a.patient_id
WHERE a.dentist_id = 101
ORDER BY a.start_at;""",
            ),
            (
                "Q3 — All SCHEDULED appointments at surgery locations",
                """SELECT
  a.appointment_id,
  a.start_at,
  a.status,
  s.surgery_id,
  s.name AS surgery_name,
  s.location_address,
  d.dentist_id,
  d.first_name AS dentist_first_name,
  d.last_name  AS dentist_last_name,
  p.patient_id,
  p.first_name AS patient_first_name,
  p.last_name  AS patient_last_name
FROM appointments a
JOIN surgeries s ON s.surgery_id = a.surgery_id
JOIN dentists  d ON d.dentist_id = a.dentist_id
JOIN patients  p ON p.patient_id = a.patient_id
WHERE a.status = 'SCHEDULED'
ORDER BY a.start_at;""",
            ),
            (
                "Q4 — Appointments for patient_id = 202 on date('2026-04-15')",
                """SELECT
  a.appointment_id,
  a.start_at,
  a.status,
  d.dentist_id,
  d.first_name AS dentist_first_name,
  d.last_name  AS dentist_last_name,
  s.surgery_id,
  s.name AS surgery_name
FROM appointments a
JOIN dentists d  ON d.dentist_id = a.dentist_id
JOIN surgeries s ON s.surgery_id = a.surgery_id
WHERE a.patient_id = 202
  AND date(a.start_at) = '2026-04-15'
ORDER BY a.start_at;""",
            ),
        ]

        for i, (title, sql) in enumerate(queries, start=1):
            cur.execute(sql)
            out = table_output(cur)
            render_text_png(title, sql, out, OUT_DIR / f"query{i}.png")

        print("Wrote", OUT_DIR)
    finally:
        con.close()


if __name__ == "__main__":
    main()
