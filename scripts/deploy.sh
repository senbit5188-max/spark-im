#!/usr/bin/env bash
# Deploy spark-im static frontends (web-chat, landing-page, admin-panel) to a remote server.
#
# Usage: scripts/deploy.sh <ssh_host> [ssh_user]
#   ssh_user defaults to "root".
#   Authentication is taken from the current ssh-agent or ~/.ssh/config — pass an SSH_KEY env
#   var if you need to point at a specific key.
#
# Required tools on the local machine: rsync, ssh, node (>=18), npm.
# The remote server must have:
#   - /opt/spark-im/{web-chat,landing-page,admin-panel} as the document roots
#   - nginx active and reload-able by the SSH user
set -euo pipefail

HOST="${1:?usage: deploy.sh <host> [user]}"
USER="${2:-root}"
REMOTE="${USER}@${HOST}"

REPO_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
WEB_DIST="${REPO_ROOT}/web-chat/dist"
LANDING_SRC="${REPO_ROOT}/landing-page"
ADMIN_SRC="${REPO_ROOT}/admin-panel"

SSH_OPTS=(-o StrictHostKeyChecking=no -o ConnectTimeout=15)
if [[ -n "${SSH_KEY:-}" ]]; then
    SSH_OPTS+=(-i "${SSH_KEY}")
fi
RSYNC_SSH="ssh ${SSH_OPTS[*]}"

echo "==> Building web-chat (production)"
(cd "${REPO_ROOT}/web-chat" && npm ci --no-audit --no-fund && NODE_ENV=production npm run build)

echo "==> Syncing web-chat -> ${REMOTE}:/opt/spark-im/web-chat"
rsync -az --delete -e "${RSYNC_SSH}" --exclude='.well-known' \
    "${WEB_DIST}/" "${REMOTE}:/opt/spark-im/web-chat/"

echo "==> Syncing landing-page -> ${REMOTE}:/opt/spark-im/landing-page"
rsync -az --delete -e "${RSYNC_SSH}" --exclude='.well-known' \
    --exclude='node_modules' --exclude='*.md' \
    "${LANDING_SRC}/" "${REMOTE}:/opt/spark-im/landing-page/"

echo "==> Syncing admin-panel -> ${REMOTE}:/opt/spark-im/admin-panel"
rsync -az --delete -e "${RSYNC_SSH}" --exclude='.well-known' --exclude='*.md' \
    "${ADMIN_SRC}/" "${REMOTE}:/opt/spark-im/admin-panel/"

echo "==> Reloading nginx on ${REMOTE}"
ssh "${SSH_OPTS[@]}" "${REMOTE}" "nginx -t && systemctl reload nginx"

echo "==> Done. Verifying endpoints..."
for url in "https://chat.telvoro.top" "https://telvoro.top" "https://admin.telvoro.top"; do
    code=$(curl -s -o /dev/null -w '%{http_code}' "${url}" || echo 000)
    echo "  ${url} -> HTTP ${code}"
done
