package net.myspring.basic.modules.hr.web.form;


import net.myspring.basic.modules.hr.domain.AuditFile;
import net.myspring.mybatis.form.BaseForm;

/**
 * Created by admin on 2017/4/6.
 */

public class AuditFileForm implements BaseForm<AuditFile> {
    private String processTypeId;

    public String getProcessTypeId() {
        return processTypeId;
    }

    public void setProcessTypeId(String processTypeId) {
        this.processTypeId = processTypeId;
    }
}
