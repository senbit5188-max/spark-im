<template>
    <div class="text-message-container"
         v-bind:class="{out:message.direction === 0}">
        <p class="text" v-html="this.$xss(this.textContent)" @mouseup="mouseUp" @contextmenu="preventContextMenuTextSelection"></p>
    </div>
</template>

<script>
import Message from "../../../../../wfc/messages/message";
import {parser as emojiParse} from "../../../../util/emoji";
import helper from "../../../../util/helper";
//import {marked} from "marked";

export default {
    name: "TextMessageContentView",
    props: {
        message: {
            type: Message,
            required: true,
        }
    },
    data() {
        return {
            textSelected: false,
        }
    },
    mounted() {
    },

    methods: {
        mouseUp(event) {
            let selection = window.getSelection().toString();
            this.textSelected = !!selection;

        },
        preventContextMenuTextSelection(event) {
            if (!this.textSelected) {
                if (window.getSelection) {
                    if (window.getSelection().empty) {  // Chrome
                        window.getSelection().empty();
                    } else if (window.getSelection().removeAllRanges) {  // Firefox
                        window.getSelection().removeAllRanges();
                    }
                } else if (document.selection) {  // IE?
                    document.selection.empty();
                }
            }
        }
    },

    computed: {
        textContent() {
            let content = this.message.messageContent.digest(this.message).trim();
            let lines = content.replace(/\r\n/g, '\n').split('\n');
            if (lines.length > 1) {
                content = lines.map(line => `<span>${helper.escapeHtml(line)}</span>\n`).reduce((total, cv, ci, arr) => total + cv, '');
            } else {
                content = helper.escapeHtml(content)
            }

            content = emojiParse(content);
            // tmp = marked.parse(tmp);
            if (content.indexOf('<img') >= 0) {
                content = content.replace(/<img/g, '<img style="max-width:400px;"')
                return content;
            }
            return content;
        }
    }
}
</script>

<style lang="css" scoped>
.text-message-container {
    margin: 0 12px;
    padding: 10px 12px;
    background-color: var(--background-message-in);
    position: relative;
    border-radius: 8px;
    display: flex;
    align-items: center;
    box-shadow: 0 1px 1px rgba(0, 0, 0, 0.04);
}

/* incoming bubble tail (left side) */
.text-message-container:not(.out)::before {
    content: "";
    position: absolute;
    left: -6px;
    top: 12px;
    width: 0;
    height: 0;
    border-style: solid;
    border-width: 6px 8px 6px 0;
    border-color: transparent var(--message-in-arrow) transparent transparent;
}

/* outgoing bubble tail (right side) */
.text-message-container.out::after {
    content: "";
    position: absolute;
    right: -6px;
    top: 12px;
    width: 0;
    height: 0;
    border-style: solid;
    border-width: 6px 0 6px 8px;
    border-color: transparent transparent transparent var(--message-out-arrow);
}

.text-message-container >>> p {
    user-select: text;
    white-space: pre-line;
}

.text-message-container >>> code {
    background: var(--background-tertiary);
    display: inline-block;
    border-radius: 3px;
    padding: 0 5px;
    user-select: text;
}

.text-message-container.out {
    background-color: var(--background-message-out);
}

.text-message-container .text {
    color: var(--text-primary);
    font-size: 14px;
    line-height: 1.5;
    max-width: 420px;
    word-spacing: normal;
    word-break: break-word;
    overflow: hidden;
    display: inline-block;
    text-overflow: ellipsis;
    user-select: text;
}

/*style for v-html */
.text-message-container .text >>> img {
    max-width: 400px !important;
    display: inline-block;
}

.text-message-container .text >>> a {
    white-space: normal;
}

.text-message-container .text >>> .emoji {
    vertical-align: middle;
}

</style>
