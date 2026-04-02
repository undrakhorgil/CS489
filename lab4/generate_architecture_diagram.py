#!/usr/bin/env python3
"""
Generate ads-system-architecture.png in the style of lab4/sample.png
(logical tiers + layers with physical tiers on the left).
Run: .venv/bin/python generate_architecture_diagram.py
"""

from pathlib import Path

from PIL import Image, ImageDraw, ImageFont

W, H = 2000, 1500
OUT = Path(__file__).resolve().parent / "ads-system-architecture.png"

# Sample-style palette: cream panels, dark borders
WHITE = (255, 255, 255)
CREAM = (255, 252, 240)
PANEL = (248, 246, 235)
SIDEBAR = (235, 235, 242)
BORDER = (40, 40, 40)
TEXT = (20, 20, 20)
TITLE = (0, 0, 80)
ARROW = (50, 50, 50)
ARROW_THICK = (30, 30, 30)
MUTED = (70, 70, 70)

SIDEBAR_W = 140
MAIN_X0 = SIDEBAR_W + 24
MAIN_X1 = W - 32


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


def text_width(draw: ImageDraw.ImageDraw, s: str, font: ImageFont.ImageFont) -> float:
    if hasattr(draw, "textlength"):
        return float(draw.textlength(s, font=font))
    bbox = draw.textbbox((0, 0), s, font=font)
    return float(bbox[2] - bbox[0])


def wrap_lines(
    text: str, font: ImageFont.ImageFont, draw: ImageDraw.ImageDraw, max_width: int
) -> list[str]:
    lines: list[str] = []
    for paragraph in text.split("\n"):
        words = paragraph.split()
        if not words:
            lines.append("")
            continue
        current: list[str] = []
        for w in words:
            trial = " ".join(current + [w])
            if text_width(draw, trial, font) <= max_width or not current:
                current.append(w)
            else:
                lines.append(" ".join(current))
                current = [w]
        if current:
            lines.append(" ".join(current))
    return lines


def draw_round_rect(
    draw: ImageDraw.ImageDraw,
    xy: tuple[int, int, int, int],
    fill: tuple[int, int, int],
    outline: tuple[int, int, int] = BORDER,
    width: int = 2,
    radius: int = 6,
) -> None:
    draw.rounded_rectangle(list(xy), radius=radius, fill=fill, outline=outline, width=width)


def draw_text_block(
    draw: ImageDraw.ImageDraw,
    xy: tuple[int, int, int, int],
    text: str,
    font: ImageFont.ImageFont,
    pad: int = 10,
) -> None:
    x0, y0, x1, y1 = xy
    mw = (x1 - x0) - 2 * pad
    lines = wrap_lines(text, font, draw, mw)
    y = y0 + pad
    for line in lines:
        draw.text((x0 + pad, y), line, fill=TEXT, font=font)
        y += int(font.size * 1.12)


def measure_block_height(
    draw: ImageDraw.ImageDraw, text: str, font: ImageFont.ImageFont, width: int, pad: int
) -> int:
    lines = wrap_lines(text, font, draw, width - 2 * pad)
    return pad * 2 + int(len(lines) * font.size * 1.12)


def arrow_v(
    draw: ImageDraw.ImageDraw,
    x: int,
    y0: int,
    y1: int,
    width: int = 2,
    head: int = 10,
) -> None:
    ya, yb = (y0, y1) if y0 < y1 else (y1, y0)
    draw.line([(x, ya), (x, yb)], fill=ARROW, width=width)
    draw.polygon([(x, yb), (x - 7, yb - head), (x + 7, yb - head)], fill=ARROW)


def arrow_h(
    draw: ImageDraw.ImageDraw,
    x0: int,
    x1: int,
    y: int,
    width: int = 2,
    double: bool = False,
) -> None:
    xa, xb = (x0, x1) if x0 < x1 else (x1, x0)
    draw.line([(xa, y), (xb, y)], fill=ARROW, width=width)
    draw.polygon([(xb, y), (xb - 12, y - 7), (xb - 12, y + 7)], fill=ARROW)
    if double:
        draw.polygon([(xa, y), (xa + 12, y - 7), (xa + 12, y + 7)], fill=ARROW)


def cloud(draw: ImageDraw.ImageDraw, cx: int, cy: int, w: int = 50, h: int = 22) -> None:
    """Small decorative cloud between physical tiers."""
    ovals = [(-18, -4, 10, 12), (-6, -8, 18, 10), (6, -4, 26, 12), (-10, 4, 14, 18)]
    for ox0, oy0, ox1, oy1 in ovals:
        draw.ellipse([cx + ox0, cy + oy0, cx + ox1, cy + oy1], outline=MUTED, width=1)


def main() -> None:
    img = Image.new("RGB", (W, H), WHITE)
    draw = ImageDraw.Draw(img)

    f_title = load_font(24)
    f_sec = load_font(15)
    f_body = load_font(13)
    f_small = load_font(11)
    f_phy = load_font(12)

    # Sidebar — physical tiers
    draw.rectangle([0, 0, SIDEBAR_W, H], fill=SIDEBAR, outline=BORDER, width=1)
    # Three zones
    z1, z2, z3 = 80, 520, 980
    draw.line([(0, z2), (SIDEBAR_W, z2)], fill=BORDER, width=1)
    draw.line([(0, z3), (SIDEBAR_W, z3)], fill=BORDER, width=1)

    draw.text((SIDEBAR_W // 2, z1 // 2 + 20), "Physical", fill=TITLE, font=f_phy, anchor="mm")
    draw.text((SIDEBAR_W // 2, z1 // 2 + 42), "tiers", fill=TITLE, font=f_phy, anchor="mm")

    # Simple tier glyphs (monitor / server / disks as text boxes)
    draw_round_rect(draw, (12, z1 + 30, SIDEBAR_W - 12, z1 + 120), CREAM)
    draw_text_block(draw, (12, z1 + 30, SIDEBAR_W - 12, z1 + 120), "Client\nTier\n(Web browser)", f_small)

    cloud(draw, SIDEBAR_W // 2, z2 - 8)

    draw_round_rect(draw, (12, z2 + 20, SIDEBAR_W - 12, z2 + 200), CREAM)
    draw_text_block(
        draw,
        (12, z2 + 20, SIDEBAR_W - 12, z2 + 200),
        "Middle\nTier\n JVM\nSpring Boot",
        f_small,
    )

    cloud(draw, SIDEBAR_W // 2, z3 - 8)

    draw_round_rect(draw, (12, z3 + 20, SIDEBAR_W - 12, H - 40), CREAM)
    draw_text_block(draw, (12, z3 + 20, SIDEBAR_W - 12, H - 40), "Data\nTier\n DB +\nSMTP", f_small)

    # Title
    draw.text(
        ((MAIN_X0 + MAIN_X1) // 2, 36),
        "Logical tiers and layers — ADS (Advantis Dental Surgeries)",
        fill=TITLE,
        font=f_title,
        anchor="mt",
    )

    y = 88

    # --- Client logical tier ---
    client_outer = (MAIN_X0, y, MAIN_X1, y + 200)
    draw_round_rect(draw, client_outer, PANEL)
    draw.text((MAIN_X0 + 12, y + 8), "Client / browser (logical)", fill=TITLE, font=f_sec)

    cw = (MAIN_X1 - MAIN_X0 - 48) // 2
    cx1, cx2 = MAIN_X0 + 16, MAIN_X0 + 24 + cw
    cy = y + 38
    ch = 150
    draw_round_rect(draw, (cx1, cy, cx1 + cw - 8, cy + ch), CREAM)
    draw_text_block(
        draw,
        (cx1, cy, cx1 + cw - 8, cy + ch),
        "Rich HTML, CSS (Bootstrap),\nJavaScript — Office Manager,\nDentist & Patient portals",
        f_body,
    )
    draw_round_rect(draw, (cx2 + 8, cy, cx2 + cw, cy + ch), CREAM)
    draw_text_block(
        draw,
        (cx2 + 8, cy, cx2 + cw, cy + ch),
        "SPA / dynamic views\nAJAX → REST\nJSON request/response",
        f_body,
    )

    # HTTPS annotation
    mid_x = (MAIN_X0 + MAIN_X1) // 2
    y_label = cy - 6
    draw.text((mid_x, y_label), "HTTPS", fill=MUTED, font=f_small, anchor="mb")

    y_after_client = client_outer[3] + 8

    # Arrows from client to server
    y_arrow_top = cy + ch
    y_arrow_bot = y_after_client + 36
    for x_off in (cw // 2 + cx1, cx2 + cw // 2):
        arrow_v(draw, x_off, y_arrow_top + 4, y_arrow_bot)

    draw.text((mid_x, y_arrow_top + 14), "REST / JSON", fill=MUTED, font=f_small, anchor="mm")
    # Thick conceptual JSON arrow (center)
    arrow_v(draw, mid_x, y_arrow_top + 28, y_arrow_bot - 4, width=4)

    y = y_arrow_bot + 4

    # --- Application server (outer) ---
    app_top = y
    app_bottom = 1180
    draw_round_rect(draw, (MAIN_X0, app_top, MAIN_X1, app_bottom), PANEL)
    draw.text((MAIN_X0 + 12, app_top + 10), "Application server — Spring Boot embedded container", fill=TITLE, font=f_sec)

    inner_x0 = MAIN_X0 + 16
    inner_x1 = MAIN_X1 - 16
    inner_y0 = app_top + 42
    inner_h = app_bottom - inner_y0 - 16
    draw_round_rect(draw, (inner_x0, inner_y0, inner_x1, inner_y0 + inner_h), WHITE)

    # Split: UI (left ~32%) vs REST app (right)
    split = inner_x0 + int((inner_x1 - inner_x0) * 0.34)
    ui_w = split - inner_x0 - 12
    rest_x0 = split + 8
    rest_x1 = inner_x1 - 100  # leave strip for Spring DI

    # UI module
    ui_y = inner_y0 + 12
    ui_h = inner_h - 24
    draw_round_rect(draw, (inner_x0 + 8, ui_y, split - 4, ui_y + ui_h), CREAM)
    draw.text((inner_x0 + 16, ui_y + 8), "User interface (deployable unit)", fill=TITLE, font=f_small)
    draw_text_block(
        draw,
        (inner_x0 + 8, ui_y + 28, split - 4, ui_y + ui_h - 8),
        "Static + templated UI\nHTML, CSS, JS assets\n(served by Spring MVC\nor separate static host)",
        f_body,
    )

    # REST module — three layers
    layer_gap = 14
    rest_w = rest_x1 - rest_x0
    strip_w = 78
    layer_x1 = rest_x1 - strip_w - 12

    h_web = 120
    h_bus = 200
    h_dao = 140
    yl = ui_y + 8

    draw_round_rect(draw, (rest_x0, yl, layer_x1, yl + h_web), CREAM)
    draw.text((rest_x0 + 10, yl + 6), "Web / API layer", fill=TITLE, font=f_small)
    draw_text_block(
        draw,
        (rest_x0 + 6, yl + 26, layer_x1 - 6, yl + h_web),
        "Spring MVC — REST controllers\nSpring Security (authZ, roles)\nOffice, Dentist, Patient APIs",
        f_body,
    )
    yl += h_web + layer_gap

    draw_round_rect(draw, (rest_x0, yl, layer_x1, yl + h_bus), CREAM)
    draw.text((rest_x0 + 10, yl + 6), "Business service layer", fill=TITLE, font=f_small)
    draw_text_block(
        draw,
        (rest_x0 + 6, yl + 26, layer_x1 - 6, yl + h_bus),
        "Domain services (POJOs):\nRegistration, Appointment\n(≤5 visits/dentist/week),\nBilling (unpaid bill gate),\nEmail notification",
        f_body,
    )
    yl += h_bus + layer_gap

    draw_round_rect(draw, (rest_x0, yl, layer_x1, yl + h_dao), CREAM)
    draw.text((rest_x0 + 10, yl + 6), "Data access layer", fill=TITLE, font=f_small)
    draw_text_block(
        draw,
        (rest_x0 + 6, yl + 26, layer_x1 - 6, yl + h_dao),
        "Spring Data JPA — repositories\n(Dentist, Patient, Surgery,\nAppointment, Bill, Account)",
        f_body,
    )

    # Spring DI vertical strip
    strip_x0 = rest_x1 - strip_w
    draw_round_rect(draw, (strip_x0, ui_y + 8, rest_x1, ui_y + ui_h - 8), (230, 238, 248))
    draw.text(
        (strip_x0 + strip_w // 2, ui_y + ui_h // 2),
        "Spring\nIoC /\nDI",
        fill=TITLE,
        font=f_small,
        anchor="mm",
    )

    # Arrow UI <-> REST (same process)
    draw.line([(split - 4, ui_y + ui_h // 2), (rest_x0, ui_y + ui_h // 2)], fill=ARROW, width=2)
    draw.polygon(
        [(rest_x0, ui_y + ui_h // 2), (rest_x0 - 10, ui_y + ui_h // 2 - 6), (rest_x0 - 10, ui_y + ui_h // 2 + 6)],
        fill=ARROW,
    )

    # --- Enterprise information services (data tier) ---
    data_y = app_bottom + 20
    data_h = 200
    draw_round_rect(draw, (MAIN_X0, data_y, MAIN_X1, data_y + data_h), PANEL)
    draw.text((MAIN_X0 + 12, data_y + 10), "Enterprise information services (data & integration)", fill=TITLE, font=f_sec)

    third = (MAIN_X1 - MAIN_X0 - 48) // 3
    dx = MAIN_X0 + 16
    dy = data_y + 44
    box_texts = [
        "Database\nPostgreSQL\n(relational store)",
        "External email\nSMTP / provider\n(appointment confirmations)",
        "Optional integrations\n(legacy EHR, billing)\n— future",
    ]
    for i, txt in enumerate(box_texts):
        x0 = MAIN_X0 + 16 + i * (third + 8)
        x1 = x0 + third - (16 if i < 2 else 8)
        draw_round_rect(draw, (x0, dy, x1, dy + 130), CREAM)
        draw_text_block(draw, (x0, dy, x1, dy + 130), txt, f_body)

    # Double-headed vertical link app <-> data tier (like sample)
    link_y_top = inner_y0 + inner_h - 6
    link_y_bot = dy - 4
    mid_link_x = (MAIN_X0 + MAIN_X1) // 2
    draw.line([(mid_link_x, link_y_top), (mid_link_x, link_y_bot)], fill=ARROW, width=2)
    # Arrowheads both ends
    draw.polygon(
        [(mid_link_x, link_y_bot), (mid_link_x - 7, link_y_bot - 12), (mid_link_x + 7, link_y_bot - 12)],
        fill=ARROW,
    )
    draw.polygon(
        [(mid_link_x, link_y_top), (mid_link_x - 7, link_y_top + 12), (mid_link_x + 7, link_y_top + 12)],
        fill=ARROW,
    )
    draw.text((mid_link_x + 86, (link_y_top + link_y_bot) // 2), "JDBC / JPA", fill=MUTED, font=f_small, anchor="lm")

    # SMTP from email service region to external email box
    smtp_x = dx + third + 8 + (third - 16) // 2
    draw.line(
        [(layer_x1 - 40, yl + h_dao // 2), (MAIN_X1 - 120, yl + h_dao // 2), (MAIN_X1 - 120, dy + 20), (smtp_x, dy + 20)],
        fill=ARROW,
        width=2,
    )
    arrow_v(draw, smtp_x, dy + 20, dy - 2)

    foot = (
        "Stack: Java 21, Spring Boot 3, Spring Web, Spring Security, Spring Data JPA, PostgreSQL, SMTP — Maven build"
    )
    draw.text((W // 2, H - 28), foot, fill=MUTED, font=f_small, anchor="ms")

    img.save(OUT, format="PNG", optimize=True)
    print("Wrote", OUT)


if __name__ == "__main__":
    main()
