<template>
  <div>
    <head-tab active="bankInForm"></head-tab>
    <div >
      <el-form :model="inputForm" ref="inputForm" :rules="rules" label-width="120px" class="form input-form">
        <el-form-item :label="$t('bankInForm.shopName')" prop="shopId">
          <depot-select :disabled="!isCreate" category="directShop" v-model="inputForm.shopId" ></depot-select>
        </el-form-item>
        <el-form-item :label="$t('bankInForm.type')" prop="type">
          <el-select :disabled="!isCreate" v-model="inputForm.type"  clearable :placeholder="$t('bankInForm.selectType')">
            <el-option v-for="item in inputForm.extra.typeList" :key="item" :label="item" :value="item"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('bankInForm.bankName')" prop="bankId">
          <bank-select v-model="inputForm.bankId" :includeCash="true" ></bank-select>
        </el-form-item>
        <el-form-item :label="$t('bankInForm.transferType')" prop="transferType">
          <el-select v-model="inputForm.transferType"  clearable>
            <el-option v-for="item in inputForm.extra.transferTypeList" :key="item" :label="item" :value="item"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('bankInForm.cash')" prop="inputDate">
          <date-picker  v-model="inputForm.inputDate"></date-picker>
        </el-form-item>
        <el-form-item :label="$t('bankInForm.amount')" prop="amount">
          <el-input v-model.number="inputForm.amount"></el-input>
        </el-form-item>
        <el-form-item :label="$t('bankInForm.serialNumber')" prop="serialNumber">
          <el-input v-model="inputForm.serialNumber"></el-input>
        </el-form-item>
        <el-form-item :label="$t('bankInForm.remarks')" prop="remarks">
          <el-input type="textarea" v-model="inputForm.remarks"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :disabled="submitDisabled" @click="formSubmit()">{{$t('bankInForm.save')}}</el-button>
        </el-form-item>
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
      return this.getData()
    },
    methods:{
      getData() {
        return{
          isCreate:this.$route.query.id==null,
          submitDisabled:false,
          inputForm:{
            extra:{}
          },
          rules: {
            shopId: [{ required: true, message: this.$t('bankInForm.prerequisiteMessage')}],
            type:[{ required: true, message: this.$t('bankInForm.prerequisiteMessage')}],
            inputDate:[{ required: true, message: this.$t('bankInForm.prerequisiteMessage')}],
            transferType:[{ required: true, message: this.$t('bankInForm.prerequisiteMessage')}],
            amount:[{ required: true, type:"number", message: this.$t('bankInForm.prerequisiteAndPositiveNumberMessage')}],
            serialNumber:[{ required: true, message: this.$t('bankInForm.prerequisiteMessage')}]
          }
        }
      },
      formSubmit(){
        this.submitDisabled = true;
        let form = this.$refs["inputForm"];
        form.validate((valid) => {
          if (valid) {
            axios.post('/api/ws/future/crm/bankIn/save', qs.stringify(util.deleteExtra(this.inputForm))).then((response)=> {
              this.$message(response.data.message);
              if(response.data.success) {
                if (this.isCreate) {
                  Object.assign(this.$data, this.getData());
                  this.initPage();
                }else {
                  util.closeAndBackToPage(this.$router,'bankInList')
                }
              }
            }).catch( () => {
              this.submitDisabled = false;
            });
          }else{
            this.submitDisabled = false;
          }
        })
      },initPage(){
        axios.get('/api/ws/future/crm/bankIn/getForm').then((response)=>{
          this.inputForm = response.data;
          if(!this.isCreate){
            axios.get('/api/ws/future/crm/bankIn/findDto',{params: {id:this.$route.query.id}}).then((response)=>{
              this.inputForm.id = response.data.id;
              this.inputForm.shopId = response.data.shopId;
              this.inputForm.type = response.data.type;
              this.inputForm.bankId = response.data.bankId;
              this.inputForm.transferType = response.data.transferType;
              this.inputForm.inputDate = response.data.inputDate;
              this.inputForm.amount = response.data.amount;
              this.inputForm.serialNumber = response.data.serialNumber;
              this.inputForm.remarks = response.data.remarks;
            });
          }
        });
      }
    },created () {
      this.initPage();
    }
  }
</script>


