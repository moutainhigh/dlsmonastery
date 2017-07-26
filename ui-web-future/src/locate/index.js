import elementId from 'element-ui/lib/locale/lang/id'
import basicHrId from"./basic_hr_id";
import basicSysId from "./basic_sys_id"
import basicSalaryId from "./basic_salary_id"
import businessBasicId from "./future_basic_id"
import businessCrmId from "./future_crm_id"
import businessLayoutId from "./future_layout_id"
import businessApiId from "./future_api_id"
import commonId from "./common_id"
import futureEnumId from "./future_enum_id"
import basicEnumId from "./basic_enum_id"
import tableDataId from "./table_data_id"

import elementZhCn from 'element-ui/lib/locale/lang/zh-CN'
import basicHrZhCn from"./basic_hr_zh-CN";
import basicSysZhCn from "./basic_sys_zh-CN"
import basicSalaryZhCn from "./basic_salary_zh-CN"
import businessBasicZhCn from "./future_basic_zh-CN"
import businessCrmZhCn from "./future_crm_zh-CN"
import businessLayoutZhCn from "./future_layout_zh-CN"
import businessApiZhCn from "./future_api_zh-CN"
import commonZhCn from "./common_zh-CN"
import futureEnumZhCn from "./future_enum_zh-CN"
import basicEnumZhCn from "./basic_enum_zh-CN"
import tableDataZhCn from "./table_data_zh-CN"

var idArray = [elementId,basicHrId,basicSysId,basicSalaryId,businessBasicId,businessCrmId,businessLayoutId,businessApiId,commonId,futureEnumId,basicEnumId,tableDataId];
var zhCnArray = [elementZhCn,basicHrZhCn,basicSysZhCn,basicSalaryZhCn,businessBasicZhCn,businessCrmZhCn,businessLayoutZhCn,businessApiZhCn,commonZhCn,futureEnumZhCn,basicEnumZhCn,tableDataZhCn];

var id = merge(idArray);
var zhCn = merge(zhCnArray);

function  merge(jsonArray) {
  var resultJsonObject = {};
  for (var index in jsonArray) {
    for (var attr in jsonArray[index]) {
      resultJsonObject[attr] = jsonArray[index][attr];
    }
  }
  return resultJsonObject;
}

export default {
  id:id,
  zhCn:zhCn
}
