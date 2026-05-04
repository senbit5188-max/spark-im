export default class WfcScheme {
    static QR_CODE_PREFIX_PC_SESSION = "sparkim://pcsession/";
    static QR_CODE_PREFIX_USER = "sparkim://user/";
    static QR_CODE_PREFIX_GROUP = "sparkim://group/";
    static QR_CODE_PREFIX_CHANNEL = "sparkim://channel/";
    static QR_CODE_PREFIX_CONFERENCE = "sparkim://conference/";

    static buildConferenceLink(conferenceId, password) {
        let link = WfcScheme.QR_CODE_PREFIX_CONFERENCE + conferenceId;
        if (password) {
            link += '/?pwd=' + password
        }
        return link;
    }
}
