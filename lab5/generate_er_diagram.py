#!/usr/bin/env python3
"""
Generate an ER diagram PNG for Lab5a (ADS).
Run from lab5/: .venv/bin/python generate_er_diagram.py
"""

from pathlib import Path

from PIL import Image, ImageDraw, ImageFont

W, H = 1800, 1200
OUT = Path(__file__).resolve().parent / "ads-er-diagram.png"

WHITE = (255, 255, 255)
BOX = (240, 248, 255)
BOX2 = (255, 250, 240)
EDGE = (30, 80, 140)
TEXT = (20, 20, 20)
LINE = (60, 60, 60)
TITLE = (10, 40, 90)
MUTED = (80, 80, 80)


def load_font(size: int) -> ImageFont.FreeTypeFont | ImageFont.ImageFont:
    for name in (
        "/System/Library/Fonts/Helvetica.ttc",
        "/System/Library/Fonts/Supplemental/Arial.ttf",
        "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
    ):
        p = Path(name)
        if p.exists():
            try:
                return ImageFont.truetype(str(p), size=size)
            except OSError:
                continue
    return ImageFont.load_default()


def wrap(draw: ImageDraw.ImageDraw, font: ImageFont.ImageFont, text: str, max_w: int) -> list[str]:
    lines: list[str] = []
    for paragraph in text.split("\n"):
        words = paragraph.split()
        if not words:
            lines.append("")
            continue
        current: list[str] = []
        for w in words:
            trial = " ".join(current + [w])
            if draw.textlength(trial, font=font) <= max_w or not current:
                current.append(w)
            else:
                lines.append(" ".join(current))
                current = [w]
        if current:
            lines.append(" ".join(current))
    return lines


def entity(
    draw: ImageDraw.ImageDraw,
    x0: int,
    y0: int,
    w: int,
    title: str,
    attrs: list[str],
    font_title: ImageFont.ImageFont,
    font_body: ImageFont.ImageFont,
    fill=BOX,
) -> tuple[int, int, int, int]:
    pad = 12
    # measure body height
    line_h = int(font_body.size * 1.25)
    h = pad * 2 + int(font_title.size * 1.4) + 8 + line_h * len(attrs)
    x1, y1 = x0 + w, y0 + h
    draw.rounded_rectangle([x0, y0, x1, y1], radius=10, fill=fill, outline=EDGE, width=2)
    draw.text((x0 + pad, y0 + pad), title, fill=TITLE, font=font_title)
    y = y0 + pad + int(font_title.size * 1.4) + 8
    draw.line([(x0 + pad, y), (x1 - pad, y)], fill=EDGE, width=2)
    y += 8
    for a in attrs:
        draw.text((x0 + pad, y), a, fill=TEXT, font=font_body)
        y += line_h
    return x0, y0, x1, y1


def crowfoot(draw: ImageDraw.ImageDraw, x: int, y: int, direction: str) -> None:
    """Tiny crow's foot marker at (x,y). direction: 'left'|'right'|'up'|'down'."""
    d = 10
    if direction == "right":
        draw.line([(x, y), (x + d, y - d)], fill=LINE, width=2)
        draw.line([(x, y), (x + d, y)], fill=LINE, width=2)
        draw.line([(x, y), (x + d, y + d)], fill=LINE, width=2)
    elif direction == "left":
        draw.line([(x, y), (x - d, y - d)], fill=LINE, width=2)
        draw.line([(x, y), (x - d, y)], fill=LINE, width=2)
        draw.line([(x, y), (x - d, y + d)], fill=LINE, width=2)
    elif direction == "down":
        draw.line([(x, y), (x - d, y + d)], fill=LINE, width=2)
        draw.line([(x, y), (x, y + d)], fill=LINE, width=2)
        draw.line([(x, y), (x + d, y + d)], fill=LINE, width=2)
    elif direction == "up":
        draw.line([(x, y), (x - d, y - d)], fill=LINE, width=2)
        draw.line([(x, y), (x, y - d)], fill=LINE, width=2)
        draw.line([(x, y), (x + d, y - d)], fill=LINE, width=2)


def main() -> None:
    img = Image.new("RGB", (W, H), WHITE)
    draw = ImageDraw.Draw(img)
    f_title = load_font(22)
    f_ent = load_font(16)
    f_attr = load_font(13)
    f_rel = load_font(14)

    draw.text((W // 2, 24), "ADS — ER Diagram (Lab5a)", fill=TITLE, font=f_title, anchor="mt")

    # Entities
    dentist = entity(
        draw,
        60,
        120,
        380,
        "DENTIST",
        [
            "PK dentist_id",
            "first_name",
            "last_name",
            "contact_phone_number",
            "email (unique)",
            "specialization",
        ],
        f_ent,
        f_attr,
        fill=BOX,
    )

    patient = entity(
        draw,
        60,
        560,
        380,
        "PATIENT",
        [
            "PK patient_id",
            "first_name",
            "last_name",
            "contact_phone_number",
            "email (unique)",
            "mailing_address",
            "date_of_birth",
        ],
        f_ent,
        f_attr,
        fill=BOX,
    )

    surgery = entity(
        draw,
        1360,
        120,
        380,
        "SURGERY",
        [
            "PK surgery_id",
            "name",
            "location_address",
            "telephone_number",
        ],
        f_ent,
        f_attr,
        fill=BOX,
    )

    appointment = entity(
        draw,
        710,
        250,
        420,
        "APPOINTMENT",
        [
            "PK appointment_id",
            "FK patient_id → PATIENT",
            "FK dentist_id → DENTIST",
            "FK surgery_id → SURGERY",
            "start_at (datetime)",
            "proposed_start_at (datetime, null)",
            "status",
            "channel",
        ],
        f_ent,
        f_attr,
        fill=BOX2,
    )

    bill = entity(
        draw,
        1360,
        620,
        380,
        "BILL",
        [
            "PK bill_id",
            "FK patient_id → PATIENT",
            "amount",
            "due_date",
            "paid (boolean)",
        ],
        f_ent,
        f_attr,
        fill=BOX2,
    )

    # Relationships (crow's foot)
    # DENTIST 1 --- * APPOINTMENT
    dx1 = dentist[2]
    dy = (dentist[1] + dentist[3]) // 2
    ax0 = appointment[0]
    ay = dy
    draw.line([(dx1, dy), (ax0, ay)], fill=LINE, width=2)
    draw.text(((dx1 + ax0) // 2, dy - 22), "1..* schedules", fill=TEXT, font=f_rel, anchor="mm")
    crowfoot(draw, ax0, ay, "left")
    draw.ellipse([dx1 - 5, dy - 5, dx1 + 5, dy + 5], outline=LINE, width=2)  # 1 side marker

    # PATIENT 1 --- * APPOINTMENT
    px1 = patient[2]
    py = (patient[1] + patient[3]) // 2
    ay2 = py
    draw.line([(px1, py), (ax0, ay2)], fill=LINE, width=2)
    draw.text(((px1 + ax0) // 2, py - 22), "1..* books", fill=TEXT, font=f_rel, anchor="mm")
    crowfoot(draw, ax0, ay2, "left")
    draw.ellipse([px1 - 5, py - 5, px1 + 5, py + 5], outline=LINE, width=2)

    # SURGERY 1 --- * APPOINTMENT
    sx0 = surgery[0]
    sy = (surgery[1] + surgery[3]) // 2
    ax1 = appointment[2]
    ay3 = sy
    draw.line([(ax1, ay3), (sx0, sy)], fill=LINE, width=2)
    draw.text(((ax1 + sx0) // 2, sy - 22), "1..* occurs_at", fill=TEXT, font=f_rel, anchor="mm")
    crowfoot(draw, ax1, ay3, "right")
    draw.ellipse([sx0 - 5, sy - 5, sx0 + 5, sy + 5], outline=LINE, width=2)

    # PATIENT 1 --- * BILL
    pxm = patient[2]
    pym = patient[1] + 40
    bx0 = bill[0]
    bym = bill[1] + 40
    draw.line([(pxm, pym), (bx0, bym)], fill=LINE, width=2)
    draw.text(((pxm + bx0) // 2, (pym + bym) // 2 - 18), "1..* has", fill=TEXT, font=f_rel, anchor="mm")
    crowfoot(draw, bx0, bym, "left")
    draw.ellipse([pxm - 5, pym - 5, pxm + 5, pym + 5], outline=LINE, width=2)

    note = (
        "Notes:\n"
        "- APPOINTMENT is the associative entity connecting PATIENT, DENTIST, and SURGERY.\n"
        "- Business rules (handled in application layer): max 5 appointments per dentist per week; \n"
        "  block new appointment requests when a PATIENT has an unpaid BILL."
    )
    draw.rounded_rectangle([620, 900, 1740, 1120], radius=10, fill=(250, 250, 250), outline=(120, 120, 120), width=2)
    draw_text = ImageDraw.Draw(img)
    y0 = 914
    for line in note.splitlines():
        draw_text.text((636, y0), line, fill=MUTED, font=f_attr)
        y0 += int(f_attr.size * 1.35)

    img.save(OUT, format="PNG", optimize=True)
    print("Wrote", OUT)


if __name__ == "__main__":
    main()

