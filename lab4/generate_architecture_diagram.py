#!/usr/bin/env python3
"""Generate ads-system-architecture.png for Lab 4. Run: .venv/bin/python generate_architecture_diagram.py"""

from pathlib import Path

from PIL import Image, ImageDraw, ImageFont

W, H = 1600, 1200
OUT = Path(__file__).resolve().parent / "ads-system-architecture.png"

# Colors
WHITE = (255, 255, 255)
BOX_FILL = (232, 244, 252)
BOX_EDGE = (21, 101, 160)
TEXT = (30, 30, 30)
TITLE = (20, 40, 80)
ARROW = (60, 60, 60)
LAYER_LABEL = (50, 50, 50)


def load_font(size: int) -> ImageFont.FreeTypeFont | ImageFont.ImageFont:
    for name in (
        "/System/Library/Fonts/Supplemental/Arial.ttf",
        "/System/Library/Fonts/Helvetica.ttc",
        "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
    ):
        p = Path(name)
        if p.exists():
            try:
                return ImageFont.truetype(str(p), size=size)
            except OSError:
                continue
    return ImageFont.load_default()


def wrap_lines(text: str, font: ImageFont.ImageFont, draw: ImageDraw.ImageDraw, max_width: int) -> list[str]:
    lines: list[str] = []
    for paragraph in text.split("\n"):
        words = paragraph.split()
        if not words:
            lines.append("")
            continue
        current: list[str] = []
        for w in words:
            trial = " ".join(current + [w])
            if draw.textlength(trial, font=font) <= max_width or not current:
                current.append(w)
            else:
                lines.append(" ".join(current))
                current = [w]
        if current:
            lines.append(" ".join(current))
    return lines


def draw_box(
    draw: ImageDraw.ImageDraw,
    xy: tuple[int, int, int, int],
    lines: list[str],
    font: ImageFont.ImageFont,
    padding: int = 12,
) -> None:
    x0, y0, x1, y1 = xy
    draw.rounded_rectangle([x0, y0, x1, y1], radius=10, fill=BOX_FILL, outline=BOX_EDGE, width=2)
    max_w = (x1 - x0) - 2 * padding
    y = y0 + padding
    for line in lines:
        draw.text((x0 + padding, y), line, fill=TEXT, font=font)
        y += int(font.size * 1.15)


def box_lines(label: str, font: ImageFont.ImageFont, draw: ImageDraw.ImageDraw, w: int) -> list[str]:
    return wrap_lines(label, font, draw, max_width=w - 24)


def measure_box_height(lines: list[str], font: ImageFont.ImageFont, padding: int) -> int:
    return padding * 2 + int(len(lines) * font.size * 1.15)


def arrow_v(draw: ImageDraw.ImageDraw, x: int, y_top: int, y_bot: int) -> None:
    draw.line([(x, y_top), (x, y_bot)], fill=ARROW, width=2)
    # arrow head
    draw.polygon([(x, y_bot), (x - 6, y_bot - 10), (x + 6, y_bot - 10)], fill=ARROW)


def arrow_h(draw: ImageDraw.ImageDraw, x0: int, x1: int, y: int) -> None:
    x_start, x_end = (x0, x1) if x0 < x1 else (x1, x0)
    draw.line([(x_start, y), (x_end, y)], fill=ARROW, width=2)
    draw.polygon([(x_end, y), (x_end - 10, y - 6), (x_end - 10, y + 6)], fill=ARROW)


def main() -> None:
    img = Image.new("RGB", (W, H), WHITE)
    draw = ImageDraw.Draw(img)
    font_title = load_font(22)
    font_l = load_font(13)
    font_s = load_font(12)
    font_xs = load_font(11)

    draw.text((W // 2, 28), "ADS — System architecture (high level)", fill=TITLE, font=font_title, anchor="mt")

    # Layer column
    lx = 24
    for ly, lbl in [
        (118, "Client tier"),
        (298, "Presentation"),
        (498, "Application / domain"),
        (738, "Data & integration"),
    ]:
        draw.text((lx, ly), lbl, fill=LAYER_LABEL, font=font_s, anchor="lm")

    # Row Y positions (tuned for layout)
    y_client = 88
    y_pres = 268
    y_app = 468
    y_data = 708

    bw1, bw2, bw3 = 480, 480, 480
    gap = 40
    x0 = 200
    x1 = x0 + bw1 + gap
    x2 = x1 + bw2 + gap

    # Clients
    c_specs = [
        (x0, "Office Manager browser\nRegister dentists & patients,\nbook appointments"),
        (x1, "Dentist portal\nView appointments & patient details"),
        (x2, "Patient portal & public form\nView, cancel, reschedule,\nrequest appointments"),
    ]
    ch = 0
    for x, text in c_specs:
        lines = box_lines(text, font_s, draw, bw1)
        h = measure_box_height(lines, font_s, 12)
        ch = max(ch, h)
    for x, text in c_specs:
        lines = box_lines(text, font_s, draw, bw1)
        draw_box(draw, (x, y_client, x + bw1, y_client + ch), lines, font_s)

    # Presentation (single box: security wraps controllers)
    pw = 1240
    px0 = 200
    p_lines = box_lines(
        "Presentation layer — Spring Security (filter chain, roles: OFFICE_MANAGER, DENTIST, PATIENT)\n"
        "+ Spring Web REST controllers (Office, Dentist, Patient routes)",
        font_s,
        draw,
        pw,
    )
    ph = measure_box_height(p_lines, font_s, 12)
    draw_box(draw, (px0, y_pres, px0 + pw, y_pres + ph), p_lines, font_s)

    # Application row
    aw = 360
    ag = 28
    ax0 = 200
    apps = [
        "Registration service\n(dentists, patients)",
        "Appointment service\nbooking, reschedule, cancel;\nmax 5 appointments/\ndentist/week",
        "Billing service\nblock new requests if\nunpaid bill outstanding",
        "Email notification\nservice (confirmations)",
    ]
    ax = ax0
    app_boxes: list[tuple[int, int, int, int]] = []
    max_ah = 0
    for label in apps:
        lines = box_lines(label, font_xs, draw, aw)
        ah = measure_box_height(lines, font_xs, 10)
        max_ah = max(max_ah, ah)
        app_boxes.append((ax, y_app, ax + aw, y_app + ah))
        ax += aw + ag
    ax = ax0
    for label in apps:
        lines = box_lines(label, font_xs, draw, aw)
        draw_box(draw, (ax, y_app, ax + aw, y_app + max_ah), lines, font_xs)
        ax += aw + ag

    # Data row
    dw1, dw2, dw3 = 520, 400, 420
    dx0, dx1, dx2 = 200, 200 + dw1 + 36, 200 + dw1 + 36 + dw2 + 36
    d1 = box_lines(
        "Repository layer\nDentist, Patient, Surgery,\nAppointment, Bill, Account",
        font_s,
        draw,
        dw1,
    )
    d2 = box_lines("Relational DB\n(e.g. PostgreSQL)", font_s, draw, dw2)
    d3 = box_lines("External email\nSMTP / provider", font_s, draw, dw3)
    dh = max(
        measure_box_height(d1, font_s, 12),
        measure_box_height(d2, font_s, 12),
        measure_box_height(d3, font_s, 12),
    )
    draw_box(draw, (dx0, y_data, dx0 + dw1, y_data + dh), d1, font_s)
    draw_box(draw, (dx1, y_data, dx1 + dw2, y_data + dh), d2, font_s)
    draw_box(draw, (dx2, y_data, dx2 + dw3, y_data + dh), d3, font_s)

    # Arrows client -> presentation
    mid_clients = [x0 + bw1 // 2, x1 + bw1 // 2, x2 + bw1 // 2]
    y_c_bot = y_client + ch
    y_p_top = y_pres
    for mx in mid_clients:
        arrow_v(draw, mx, y_c_bot + 4, y_p_top - 4)

    # Presentation -> application
    mid_pres = px0 + pw // 2
    arrow_v(draw, mid_pres, y_pres + ph + 4, y_app - 4)

    # Application -> data (from middle of app row)
    mid_app = ax0 + (4 * aw + 3 * ag) // 2
    arrow_v(draw, mid_app, y_app + max_ah + 4, y_data - 4)

    # Repos -> DB
    arrow_h(draw, dx0 + dw1 - 8, dx1 + 8, y_data + dh // 2)

    # Email service -> external (from last app box center to email external)
    ex_mx = dx2 + dw3 // 2
    last_app_cx = ax0 + 3 * (aw + ag) + aw // 2
    y_mid = y_app + max_ah + (y_data - (y_app + max_ah)) // 2
    draw.line([(last_app_cx, y_app + max_ah + 4), (last_app_cx, y_mid), (ex_mx, y_mid), (ex_mx, y_data - 4)], fill=ARROW, width=2)
    draw.polygon([(ex_mx, y_data - 4), (ex_mx - 6, y_data - 14), (ex_mx + 6, y_data - 14)], fill=ARROW)

    foot = (
        "Tech stack: Java 21, Spring Boot 3, Spring Web, Spring Security, JPA/PostgreSQL (prod), SMTP email, Maven — "
        "Cross-cutting: validation, exception handling, logging"
    )
    draw.text((W // 2, H - 36), foot, fill=(80, 80, 80), font=font_xs, anchor="ms")

    img.save(OUT, format="PNG", optimize=True)
    print("Wrote", OUT)


if __name__ == "__main__":
    main()
