#!/usr/bin/env python3
"""Rebrand telegram-tt: Telegram Web -> 星火 IM"""
import json
import os
import re
from pathlib import Path

ROOT = Path("/opt/spark-im/telegram-tt")
BRAND_NAME = "\u661f\u706b IM"  # 星火 IM
BRAND_DESC = "\u661f\u706b IM \u00b7 \u5b89\u5168 \u00b7 \u9ad8\u6548 \u00b7 \u5373\u65f6\u901a\u8baf"  # 星火 IM · 安全 · 高效 · 即时通讯
THEME_COLOR = "#3b82f6"
BG_COLOR = "#0c1e4a"

# 1. Update all webmanifest files
for mf in [
    "public/site.webmanifest",
    "public/site_apple.webmanifest",
    "public/site_dev.webmanifest",
    "public/site_apple_dev.webmanifest",
]:
    p = ROOT / mf
    if not p.exists():
        print(f"skip (missing): {mf}")
        continue
    with open(p, "r", encoding="utf-8") as f:
        data = json.load(f)
    data["name"] = BRAND_NAME
    data["short_name"] = BRAND_NAME
    data["description"] = BRAND_DESC
    data["theme_color"] = THEME_COLOR
    data["background_color"] = BG_COLOR
    with open(p, "w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False, indent=4)
    print(f"updated: {mf}")

# 2. Update .env (APP_TITLE / APP_NAME)
env_path = ROOT / ".env"
lines = env_path.read_text(encoding="utf-8").splitlines()
keys_seen = set()
for i, line in enumerate(lines):
    if line.startswith("APP_TITLE="):
        lines[i] = f"APP_TITLE={BRAND_NAME}"
        keys_seen.add("APP_TITLE")
    elif line.startswith("APP_NAME="):
        lines[i] = f"APP_NAME={BRAND_NAME}"
        keys_seen.add("APP_NAME")
if "APP_TITLE" not in keys_seen:
    lines.append(f"APP_TITLE={BRAND_NAME}")
if "APP_NAME" not in keys_seen:
    lines.append(f"APP_NAME={BRAND_NAME}")
env_path.write_text("\n".join(lines) + "\n", encoding="utf-8")
print(f"updated: .env")
print(env_path.read_text(encoding="utf-8"))

# 3. Patch webpack config DEFAULT_APP_TITLE so even if env not picked up, default is right
wp = ROOT / "webpack.config.ts"
text = wp.read_text(encoding="utf-8")
new_text = re.sub(
    r"const DEFAULT_APP_TITLE = `Telegram\$\{APP_ENV !== 'production' \? ' Beta' : ''\}`;",
    "const DEFAULT_APP_TITLE = `\u661f\u706b IM${APP_ENV !== 'production' ? ' Beta' : ''}`;",
    text,
)
if new_text != text:
    wp.write_text(new_text, encoding="utf-8")
    print("updated: webpack.config.ts DEFAULT_APP_TITLE")
else:
    print("skip: webpack.config.ts (no match)")

# 4. Patch src/config.ts APP_NAME default
cfg = ROOT / "src/config.ts"
text = cfg.read_text(encoding="utf-8")
new_text = re.sub(
    r"export const APP_NAME = process\.env\.APP_NAME \|\| `Telegram Web \$\{APP_CODE_NAME\}`;",
    f"export const APP_NAME = process.env.APP_NAME || `{BRAND_NAME}`;",
    text,
)
if new_text != text:
    cfg.write_text(new_text, encoding="utf-8")
    print("updated: src/config.ts APP_NAME")
else:
    print("skip: src/config.ts (no match)")

print("\nDone.")
