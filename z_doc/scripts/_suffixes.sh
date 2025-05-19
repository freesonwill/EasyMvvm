#!/bin/bash
# 判断是否有传入目录参数
if [[ $# -eq 0 ]]; then
  echo "Usage: sh gradle/_suffixes.sh module_live 或者 sh gradle/_suffixes.sh --dir=module_live"
  exit 1
fi

THEMES=$(grep -E '^Themes[[:space:]]*=' gradle.properties | sed 's/.*=[[:space:]]*//')
THEMES=$(echo "$THEMES" | tr -d '\r' | tr -d ' ')

PROJECT_DIR="$(pwd)"

for arg in "$@"; do
    case $arg in
        --dir=*)
            PROJECT_DIR="${arg#*=}"
            ;;
        *)
            PROJECT_DIR="$arg"
            ;;
    esac
done

IFS=',' read -ra THEME_ARRAY <<< "$THEMES"

echo "🎨 Processing themes: $THEMES"

for THEME in "${THEME_ARRAY[@]}"; do
    RES_DIR="$PROJECT_DIR/src/main/res-$THEME"
    SUFFIX="_$THEME"

    if [[ ! -d "$RES_DIR" ]]; then
        echo "⚠️ Directory not found: $RES_DIR"
        continue
    fi

    echo "🎨 Processing theme: $THEME"

    for TYPE in drawable mipmap; do
        TARGET_DIR="$RES_DIR/$TYPE"
        if [[ -d "$TARGET_DIR" ]]; then
            for FILE in "$TARGET_DIR"/*.*; do
                [[ -f "$FILE" ]] || continue
                BASENAME=$(basename "$FILE")
                NAME="${BASENAME%.*}"
                EXT="${BASENAME##*.}"

                if [[ "$NAME" != *"$SUFFIX" ]]; then
                    NEW_NAME="${NAME}${SUFFIX}.${EXT}"
                    mv "$FILE" "$TARGET_DIR/$NEW_NAME"
                    echo "📝 Renamed: $BASENAME -> $NEW_NAME"
                fi
            done
        fi
    done

    VALUES_DIR="$RES_DIR/values"
    if [[ -d "$VALUES_DIR" ]]; then
        for COLOR_FILE in "$VALUES_DIR"/colors*.xml; do
            [[ -f "$COLOR_FILE" ]] || continue

            sed -E -i '' '/<color name="[^"]+'"$SUFFIX"'">/! s|<color name="([^"]+)">|<color name="\1'"$SUFFIX"'">|g' "$COLOR_FILE"

            echo "🎨 Modified: $(basename "$COLOR_FILE")"
        done
    fi
done

echo "✅ Done."
