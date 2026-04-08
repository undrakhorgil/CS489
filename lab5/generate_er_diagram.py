#!/usr/bin/env python3
"""
Generate an ER diagram PNG for Lab5a (ADS).
Run from lab5/: .venv/bin/python generate_er_diagram.py
"""

from pathlib import Path

from PIL import Image, ImageDraw, ImageFont

W, H = 2000, 1200
OUT = Path(__file__).resolve().parent / "ads-er-diagram.png"

WHITE = (255, 255, 255)
BOX = (238, 247, 255)
BOX2 = (255, 252, 242)
EDGE = (25, 86, 156)
TEXT = (20, 20, 20)
LINE = (60, 60, 60)
TITLE = (10, 40, 90)
MUTED = (80, 80, 80)
BG_DOT = (245, 245, 248)
REL = (25, 25, 25)


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
    draw.rounded_rectangle([x0, y0, x1, y1], radius=12, fill=fill, outline=EDGE, width=2)
    draw.text((x0 + pad, y0 + pad), title, fill=TITLE, font=font_title)
    y = y0 + pad + int(font_title.size * 1.4) + 8
    draw.line([(x0 + pad, y), (x1 - pad, y)], fill=EDGE, width=2)
    y += 8
    for a in attrs:
        color = TEXT
        if a.startswith("PK "):
            color = (9, 60, 130)
        elif a.startswith("FK "):
            color = (95, 50, 10)
        draw.text((x0 + pad, y), a, fill=color, font=font_body)
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


def one_marker(draw: ImageDraw.ImageDraw, x: int, y: int) -> None:
    draw.ellipse([x - 5, y - 5, x + 5, y + 5], outline=LINE, width=2)


def connect_ortho(
    draw: ImageDraw.ImageDraw,
    start: tuple[int, int],
    end: tuple[int, int],
    mid_x: int,
    width: int = 2,
) -> None:
    """Orthogonal connector: start -> (mid_x,start_y) -> (mid_x,end_y) -> end."""
    x0, y0 = start
    x1, y1 = end
    draw.line([(x0, y0), (mid_x, y0), (mid_x, y1), (x1, y1)], fill=LINE, width=width, joint="curve")


def main() -> None:
    img = Image.new("RGB", (W, H), WHITE)
    draw = ImageDraw.Draw(img)
    f_title = load_font(24)
    f_ent = load_font(18)
    f_attr = load_font(13)
    f_rel = load_font(14)

    # Subtle dotted background (helps alignment and readability)
    for y in range(0, H, 24):
        for x in range(0, W, 24):
            draw.ellipse([x - 1, y - 1, x + 1, y + 1], fill=BG_DOT)

    draw.text((W // 2, 24), "ADS — ER Diagram (Lab5a)", fill=TITLE, font=f_title, anchor="mt")

    # Entities
    dentist = entity(
        draw,
        90,
        120,
        420,
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
        90,
        560,
        420,
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
        1490,
        120,
        420,
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
        790,
        260,
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
        1490,
        600,
        420,
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

    # Relationships (crow's foot) - orthogonal connectors for a cleaner look
    ax0 = appointment[0]
    ax1 = appointment[2]

    # DENTIST (1) — (0..*) APPOINTMENT
    d_out = (dentist[2], (dentist[1] + dentist[3]) // 2)
    a_in_d = (ax0, d_out[1])
    connect_ortho(draw, d_out, a_in_d, mid_x=680)
    one_marker(draw, d_out[0], d_out[1])
    crowfoot(draw, a_in_d[0], a_in_d[1], "left")
    draw.text((680, d_out[1] - 18), "1", fill=REL, font=f_rel, anchor="mm")
    draw.text((ax0 - 18, d_out[1] - 18), "0..*", fill=REL, font=f_rel, anchor="mm")

    # PATIENT (1) — (0..*) APPOINTMENT
    p_out = (patient[2], (patient[1] + patient[3]) // 2)
    a_in_p = (ax0, p_out[1])
    connect_ortho(draw, p_out, a_in_p, mid_x=680)
    one_marker(draw, p_out[0], p_out[1])
    crowfoot(draw, a_in_p[0], a_in_p[1], "left")
    draw.text((680, p_out[1] - 18), "1", fill=REL, font=f_rel, anchor="mm")
    draw.text((ax0 - 18, p_out[1] - 18), "0..*", fill=REL, font=f_rel, anchor="mm")

    # SURGERY (1) — (0..*) APPOINTMENT
    s_out = (surgery[0], (surgery[1] + surgery[3]) // 2)
    a_in_s = (ax1, s_out[1])
    connect_ortho(draw, a_in_s, s_out, mid_x=1360)
    one_marker(draw, s_out[0], s_out[1])
    crowfoot(draw, a_in_s[0], a_in_s[1], "right")
    draw.text((1360, s_out[1] - 18), "0..*", fill=REL, font=f_rel, anchor="mm")
    draw.text((s_out[0] + 22, s_out[1] - 18), "1", fill=REL, font=f_rel, anchor="mm")

    # PATIENT (1) — (0..*) BILL
    p_out2 = (patient[2], patient[3] - 36)
    b_in = (bill[0], bill[1] + 48)
    connect_ortho(draw, p_out2, b_in, mid_x=1120)
    one_marker(draw, p_out2[0], p_out2[1])
    crowfoot(draw, b_in[0], b_in[1], "left")
    draw.text((1120, p_out2[1] - 18), "1", fill=REL, font=f_rel, anchor="mm")
    draw.text((bill[0] - 18, b_in[1] - 18), "0..*", fill=REL, font=f_rel, anchor="mm")

    # (Removed notes panel per request)

    img.save(OUT, format="PNG", optimize=True)
    print("Wrote", OUT)


if __name__ == "__main__":
    main()

