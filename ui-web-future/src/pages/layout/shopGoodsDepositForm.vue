<template>
  <div>
    <head-tab active="shopGoodsDepositForm"></head-tab>
    <div>
      <el-form :model="inputForm" ref="inputForm" :rules="rules" label-width="120px"  class="form input-form">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item :label="$t('shopGoodsDepositForm.shopName')" prop="shopId" >
              <depot-select  :disabled="!isCreate" category="shop" v-model="inputForm.shopId"  @input="shopIdChanged"></depot-select>
            </el-form-item>
            <el-form-item :label="$t('shopGoodsDepositForm.bank')" prop="bankId" >
              <bank-select v-model="inputForm.bankId" :includeCash="true"></bank-select>
            </el-form-item>
            <el-form-item :label="$t('shopGoodsDepositForm.amount')" prop="amount">
              <el-input  v-model.number="inputForm.amount"></el-input>
            </el-form-item>
            <el-form-item v-if="isCreate" :label="$t('shopGoodsDepositForm.department')" prop="departMent">
              <el-select v-model="inputForm.departMent" >
                <el-option v-for="item in inputForm.extra.departMentList" :key="item.fnumber"  :label="item.ffullName" :value="item.fnumber"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item :label="$t('shopGoodsDepositForm.remarks')" prop="remarks" >
              <el-input type="textarea" v-model="inputForm.remarks"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :disabled="submitDisabled" @click="formSubmit()" >{{$t('shopGoodsDepositForm.save')}}</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>
  </div>
</template>
<script>

  import depotSelect from 'components/future/depot-select'

  import bankSelect from 'components/future/bank-select'

  export default{
    components:{
      depotSelect,
      bankSelect
    },

      data(){
        return this.getData();
      },
    methods:{
      getData(){
        return {
          isCreate: this.$route.query.id == null,
          submitDisabled: false,
          inputForm:{
            extra:{},
          },
          rules: {
            shopId: [{required: true, message: this.$t('shopGoodsDepositForm.prerequisiteMessage')}],
            bankId: [{required: true, message: this.$t('shopGoodsDepositForm.prerequisiteMessage')}],
            amount: [{required: true, type:"number", message: this.$t('shopGoodsDepositForm.prerequisiteAndNumberMessage')}],
            departMent: [{required: true,  message: this.$t('shopGoodsDepositForm.prerequisiteMessage')}],
          },
        };
      },
      formSubmit(){
        this.submitDisabled = true;
          let form = this.$refs["inputForm"];
          form.validate((valid) => {
            if (valid) {
              axios.post('/api/ws/future/crm/shopGoodsDeposit/save', qs.stringify(util.deleteExtra(this.inputForm))).then((response)=> {
                this.$message(response.data.message);
                this.submitDisabled = false;
                if(response.data.success) {
                  if (this.isCreate) {
                    Object.assign(this.$data, this.getData());
                    this.initPage();
                  }else{
                    util.closeAndBackToPage(this.$router,'shopGoodsDepositList')
                  }
                }
              }).catch(() => {
                this.submitDisabled = false;
              });
            }else{
              this.submitDisabled = false;
            }
          })
        },shopIdChanged(){
            if(!this.isCreate){ //界面在修改的时候，不可以改变shopId
                return ;
            }
          axios.get('/api/ws/future/basic/depot/getDefaultDepartMent',{params: {depotId:this.inputForm.shopId}}).then((response)=>{
            this.inputForm.departMent  = response.data;
          });

        },initPage(){
        axios.get('/api/ws/future/crm/shopGoodsDeposit/getForm').then((response)=>{
          this.inputForm = response.data;

          axios.get('/api/ws/future/crm/shopGoodsDeposit/findDto',{params: {id: this.$route.query.id}}).then((response)=>{
              this.inputForm.id = response.data.id;
              this.inputForm.shopId = response.data.shopId;
              this.inputForm.bankId = response.data.bankId;
              this.inputForm.amount = response.data.amount;
              this.inputForm.departMent = response.data.departMent;
              this.inputForm.remarks = response.data.remarks;
          });

        });
      }
    },created(){
      this.initPage();
    }
  }
</script>
