<template>
  <div>
    <head-tab active="depotAccountDetail"></head-tab>
    <div>
      <el-table :data="depotAccountDetailList"  :pageHeight="pageHeight" style="margin-top:5px;"  :element-loading-text="$t('depotAccountDetail.loading')" stripe border :row-class-name="tableRowClassName">
        <el-table-column prop="billType" :label="$t('depotAccountDetail.billType')"></el-table-column>
        <el-table-column prop="billNo" :label="$t('depotAccountDetail.billNo')"></el-table-column>
        <el-table-column prop="billDate" :label="$t('depotAccountDetail.date')"></el-table-column>
        <el-table-column prop="materialName" :label="$t('depotAccountDetail.materialName')"></el-table-column>
        <el-table-column prop="qty" :label="$t('depotAccountDetail.qty')"></el-table-column>
        <el-table-column prop="price" :label="$t('depotAccountDetail.price')"></el-table-column>
        <el-table-column prop="totalAmount" :label="$t('depotAccountDetail.amount')"></el-table-column>
        <el-table-column prop="realGet" :label="$t('depotAccountDetail.receiveAmount')"></el-table-column>
        <el-table-column prop="shouldGet" :label="$t('depotAccountDetail.shouldGet')"></el-table-column>
        <el-table-column prop="endShouldGet" :label="$t('depotAccountDetail.endShouldGet')"></el-table-column>
        <el-table-column prop="remarks" :label="$t('depotAccountDetail.remarks')"></el-table-column>
      </el-table>
    </div>
  </div>
</template>
<script>
  export default{
    data(){
      return{
        depotAccountDetailList:[],
        pageHeight: 600,
        }
    },
    methods:{
      tableRowClassName(row, index) {

        if (row.billStatus !== "C") {
          return 'danger-row';
        }
        return '';
      }
    },
    created(){
       this.pageHeight = 0.74*window.innerHeight;

      axios.get('/api/ws/future/basic/depot/findDepotAccountDetailList',{params:{clientOutId:this.$route.query.clientOutId, dateRange:this.$route.query.dateRange}} ).then((response)=>{

          if(response.data){
            this.depotAccountDetailList=response.data;
          }else{
            this.depotAccountDetailList=[];
          }
        })
    }
  }
</script>
<style>
  .el-table .danger-row {
    background: #DC143C;
  }
</style>
