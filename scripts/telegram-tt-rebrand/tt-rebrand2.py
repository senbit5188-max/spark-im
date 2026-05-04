#!/usr/bin/env python3
"""Patch remaining Telegram brand strings in src/index.html etc."""
from pathlib import Path

ROOT = Path("/opt/spark-im/telegram-tt")
BRAND_NAME = "\u661f\u706b IM"
BRAND_DESC = "\u661f\u706b IM \u00b7 \u5b89\u5168 \u00b7 \u9ad8\u6548 \u00b7 \u5373\u65f6\u901a\u8baf"

# src/index.html
p = ROOT / "src/index.html"
text = p.read_text(encoding="utf-8")
old_h1 = "<h1>Telegram Web</h1>"
new_h1 = f"<h1>{BRAND_NAME}</h1>"
text2 = text.replace(old_h1, new_h1)

old_desc = "Telegram is a cloud-based mobile and desktop messaging app with a focus on security and speed."
text2 = text2.replace(old_desc, BRAND_DESC)
if text2 != text:
    p.write_text(text2, encoding="utf-8")
    print("updated: src/index.html")
else:
    print("skip: src/index.html")

# Also update telegram url references (og:url etc) — leave them, they're minor

print("Done.")
