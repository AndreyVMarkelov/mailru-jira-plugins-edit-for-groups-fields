// Created by Andrey Markelov 19-11-2012.
// Copyright Mail.Ru Group 2012. All rights reserved.

//--> init settings dialog
function initSettingsDlg(baseUrl, cfId)
{
    var res = "";
    jQuery.ajax({
        url: baseUrl + "/rest/editgroupscfws/1.0/groupsenabledfieldws/initconfdialog",
        type: "POST",
        dataType: "json",
        data: {"cfId": cfId, "atl_token": jQuery("#atl_token")},
        async: false,
        error: function(xhr, ajaxOptions, thrownError) {
            try {
                var respObj = eval("(" + xhr.responseText + ")");
                initErrorDlg(respObj.message).show();
            } catch(e) {
                initErrorDlg(xhr.responseText).show();
            }
        },
        success: function(result) {
            res = result.html;
        }
    });

    return res;
}

AJS.$(document).ready(function() {
    jQuery(window).bind('beforeunload', function() {
        
    });
});

//--> configure enabled for groups field
function configureEnabledForGroupsField(event, baseUrl, cfId) {
    event.preventDefault();

    var dialogBody = initSettingsDlg(baseUrl, cfId);
    if (!dialogBody)
    {
        return;
    }

    jQuery("#configure_cf_dialog").remove();
    var md = new AJS.Dialog({
        width:550,
        height:350,
        id:"configure_cf_dialog",
        closeOnOutsideClick: true
    });
    md.addHeader(AJS.I18n.getText("edit-groups-cf.configurefieldtitle"));
    md.addPanel("load_panel", dialogBody);
    md.addButton(AJS.I18n.getText("edit-groups-cf.applybutton"), function() {
        jQuery.ajax({
            url: baseUrl + "/rest/editgroupscfws/1.0/groupsenabledfieldws/configurefield",
            type: "POST",
            dataType: "json",
            data: AJS.$("#editgroupform").serialize(),
            async: false,
            error: function(xhr, ajaxOptions, thrownError) {
                var errText;
                try {
                    var respObj = eval("(" + xhr.responseText + ")");
                    if (respObj.message) {
                        errText = respObj.message;
                    } else if (respObj.html) {
                        errText = respObj.html;
                    } else {
                        errText = xhr.responseText;
                    }
                } catch(e) {
                    errText = xhr.responseText;
                }
                jQuery("#errorpart").empty();
                jQuery("#errorpart").append("<div class='errdiv'>" + errText + "</div>");
            },
            success: function(result) {
                document.location.reload(true);
            }
        });
    });
    md.addCancel(AJS.I18n.getText("edit-groups-cf.closebutton"), function() {
        md.hide();
    });
    md.show();
}

//--> initialize error dialog
function initErrorDlg(bodyText) {
    var errorDialog = new AJS.Dialog({
        width:420,
        height:250,
        id:"error-dialog",
        closeOnOutsideClick: true
    });

    errorDialog.addHeader(AJS.I18n.getText("edit-groups-cf.errordialogtitle"));
    errorDialog.addPanel("ErrorMainPanel", '' +
        '<html><body><div class="error-message errdlg">' +
        bodyText +
        '</div></body></html>',
        "error-panel-body");
    errorDialog.addCancel(AJS.I18n.getText("edit-groups-cf.closebutton"), function() {
        errorDialog.hide();
    });

    return errorDialog;
}
