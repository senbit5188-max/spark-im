#!/usr/bin/env bash
# Build telegram-tt (Telegram Web A fork, Ajaxy/telegram-tt, GPL-3.0) with
# spark-im branding ("星火 IM", spark logo) and deploy to /opt/spark-im/telegram/.
#
# Run on the production server (Ubuntu 24.04):
#   sudo bash scripts/telegram-tt-rebrand/build-and-deploy.sh
#
# Pre-requisites (one-time):
#   - Node 22.18+ (we use n: `npm i -g n && n install 22.20.0`)
#   - pnpm 10+ (apt installs `pnpm`)
#   - python3
#
# Required env vars:
#   TELEGRAM_API_ID, TELEGRAM_API_HASH (from https://my.telegram.org/apps)

set -euo pipefail

SRC_REPO=/opt/spark-im/source
TT_DIR=/opt/spark-im/telegram-tt
DEPLOY_DIR=/opt/spark-im/telegram

API_ID="${TELEGRAM_API_ID:?need TELEGRAM_API_ID}"
API_HASH="${TELEGRAM_API_HASH:?need TELEGRAM_API_HASH}"

# 1) Clone telegram-tt if absent
if [[ ! -d "$TT_DIR" ]]; then
    git clone --depth 1 https://github.com/Ajaxy/telegram-tt.git "$TT_DIR"
fi

cd "$TT_DIR"

# 2) Write .env
cat > .env <<EOF
NODE_ENV=production
TELEGRAM_API_ID=${API_ID}
TELEGRAM_API_HASH=${API_HASH}
BASE_URL=/telegram/
EOF

# 3) Copy spark-im branded icons into telegram-tt/public
cp "$SRC_REPO/scripts/telegram-tt-rebrand/icons/"*.{png,ico,svg} public/

# 4) Run rebrand python (overrides app title, manifests, etc to "星火 IM")
python3 "$SRC_REPO/scripts/telegram-tt-rebrand/tt-rebrand.py"
python3 "$SRC_REPO/scripts/telegram-tt-rebrand/tt-rebrand2.py"

# 5) Install dependencies + build
pnpm install --shamefully-hoist
pnpm run build:production

# 6) Deploy (excluding source maps for size)
rsync -a --delete \
    --exclude='*.map' \
    --exclude='build-stats.json' \
    --exclude='statoscope-report.html' \
    "$TT_DIR/dist/" "$DEPLOY_DIR/"

# 7) Fix permissions for nginx (www-data needs r on files, rx on dirs)
find "$DEPLOY_DIR" -type f -exec chmod 644 {} +
find "$DEPLOY_DIR" -type d -exec chmod 755 {} +

# 8) Verify
echo "==> Telegram Web A rebrand complete."
echo "    Title bytes (should contain 星火 IM):"
grep -oE '<title>[^<]+</title>' "$DEPLOY_DIR/index.html" | head -1 | xxd | head -2
echo "==> Smoke test:"
curl -sI -H 'Host: chat.telvoro.top' --resolve chat.telvoro.top:443:127.0.0.1 \
    https://chat.telvoro.top/telegram/ -k | head -3
