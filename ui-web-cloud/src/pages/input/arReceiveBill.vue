<template>
  <div>
    <head-tab active="arReceiveBill"></head-tab>
    <el-row :gutter="20">
      <el-form :model="formData" method="get" ref="inputForm" :rules="rules">
        <el-col :span="6">
          <el-button type="primary" :disabled="submitDisabled" @click="formSubmit" icon="check">保存</el-button>
        </el-col>
      </el-form>
    </el-row>
    <el-row style="margin-top: 20px">
     <div id="grid" ref="handsontable" style="width:100%;height:600px;overflow:hidden;"></div>
    </el-row>
  </div>
</template>
<style>
  @import "~handsontable/dist/handsontable.full.css";
</style>
<script>
  import Handsontable from 'handsontable/dist/handsontable.full.js';
  var table = null;
  export default {
    data:function () {
      return this.getData();
    },
    methods: {
      getData() {
        return {
          settings: {
            rowHeaders:true,
            autoColumnSize:true,
            manualColumnResize:true,
            stretchH: 'all',
            height: 650,
            minSpareRows: 1,
            fixedRowsTop:0,
            colHeaders: ["往来单位", "我方银行账号", "业务日期", "收款金额", "结算方式", "备注"],
            columns: [
              {type: "autocomplete", strict: true, allowEmpty: false, customerName:[],source: this.customerName},
              {type: "autocomplete", strict: true, allowEmpty: true, bankAcntName:[],source: this.bankAcntName},
              {type: 'date',strict: true, allowEmpty: false,dateFormat:'YYYY-MM-DD',correctFormat: true},
              {type: "numeric",strict: true,allowEmpty: false, format:"0,0.00"},
              {type: "autocomplete",strict: true, allowEmpty: false, settleTypeName:[],source: this.settleTypeName},
              {type: "text",strict: true, allowEmpty: false }
            ],
             contextMenu: util.contextMenu(this.$store.state.global.lang),
            afterChange: function (changes, source) {
              if (source !== 'loadData') {
                for (let i = changes.length - 1; i >= 0; i--) {
                  let row = changes[i][0];
                  let column = changes[i][1];
                  if(column === 4 &&　changes[i][3] === '现金') {
                    table.setDataAtCell(row, 1, '');
                    table.setDataAtCell(row, 5, '批量开单');
                  }
                }
              }
            }
          },
          formData:{
          },rules: {},
          submitDisabled:false
        };
      },
      formSubmit(){
        this.submitDisabled = true;
        var form = this.$refs["inputForm"];
        form.validate((valid) => {
          if (valid) {
            this.formData.json =new Array();
            let list = table.getData();
            for(let item in list){
              if(!table.isEmptyRow(item)){
                this.formData.json.push(list[item]);
              }
            }
            this.formData.json = JSON.stringify(this.formData.json);
            var submitData = util.deleteExtra(this.formData);
            axios.post('/api/global/cloud/input/arReceiveBill/save', qs.stringify(submitData,{allowDots:true})).then((response)=> {
              if(response.data.success){
                this.$message(response.data.message);
                this.initPage();
                form.resetFields();
              }else{
                this.$alert(response.data.message);
              }
              this.submitDisabled = false;
            }).catch(function () {
              this.submitDisabled = false;
            });
          }else{
            this.submitDisabled = false;
          }
        })
      },
      initPage() {
        table = new Handsontable(this.$refs["handsontable"], this.settings);
      },
    },
    created() {
      axios.get('/api/global/cloud/input/arReceiveBill/form').then((response)=>{
        this.formData = response.data;
        let extra = response.data.extra;
        this.settings.columns[0].source = extra.customerNameList;
        this.settings.columns[1].source = extra.banAcntNameList;
        this.settings.columns[4].source = extra.settleTypeNameList;
        this.initPage();
      });
    },
  }
</script>
