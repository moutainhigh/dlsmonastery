<template>
  <div>
    <head-tab active="goodsOrderShipList"></head-tab>
    <div>
      <el-row>
        <el-button type="primary" @click="formVisible = true" icon="search">{{ $t('goodsOrderShipList.filter') }}</el-button>
        <el-button v-permit="'crm:goodsOrderShip:ship'" type="primary" @click="itemShip(null)">{{ $t('goodsOrderShipList.ship') }}</el-button>
        <span v-html="searchText"></span>
      </el-row>
      <search-dialog @enter="search()" :show="formVisible" @hide="formVisible=false" :title="$t('goodsOrderShipList.filter')" v-model="formVisible" size="large" class="search-form" z-index="1500" ref="searchDialog">
        <el-form :model="formData" :label-width="formLabelWidth">
          <el-row :gutter="4">
            <el-col :span="8">
              <el-form-item :label="$t('goodsOrderShipList.netType')" >
                <el-select v-model="formData.netType" clearable filterable :placeholder="$t('goodsOrderShipList.selectNetType')">
                  <el-option v-for="netType in formData.extra.netTypeList" :key="netType" :label="netType" :value="netType"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item :label="$t('goodsOrderShipList.businessId')"  >
                <el-input v-model.number="formData.businessId" :placeholder="$t('goodsOrderShipList.likeSearch')"></el-input>
              </el-form-item>
              <el-form-item :label="$t('goodsOrderShipList.billDate')" >
                <date-range-picker  v-model="formData.billDateRange" ></date-range-picker>
              </el-form-item>
              <el-form-item :label="$t('goodsOrderShipList.shipType')" >
                <el-select v-model="formData.shipType" clearable filterable :placeholder="$t('goodsOrderShipList.selectShopType')">
                  <el-option v-for="shipType in formData.extra.shipTypeList" :key="shipType" :label="shipType" :value="shipType"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item :label="$t('goodsOrderShipList.office')" >
                <office-select v-model="formData.areaId" :remote="false" officeRuleName="办事处" @afterInit="setSearchText"></office-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item :label="$t('goodsOrderShipList.shipDate')" >
                <date-range-picker  v-model="formData.shipDateRange" ></date-range-picker>
              </el-form-item>
              <el-form-item :label="$t('goodsOrderShipList.shop')" >
                <el-input v-model="formData.shopName" :placeholder="$t('goodsOrderShipList.likeSearch')"></el-input>
              </el-form-item>
              <el-form-item :label="$t('goodsOrderShipList.store')" >
                <depot-select v-model="formData.storeIdList" :multiple="true" category="store" @afterInit="setSearchText"></depot-select>
              </el-form-item>
              <el-form-item :label="$t('goodsOrderShipList.createdBy')">
                <account-select v-model="formData.createdBy" @afterInit="setSearchText" ></account-select>
              </el-form-item>
              <el-form-item :label="$t('goodsOrderShipList.outCode')" >
                <el-input v-model="formData.outCode" :placeholder="$t('goodsOrderShipList.likeSearch')" ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item :label="$t('goodsOrderShipList.createdDate')" >
                <date-range-picker v-model="formData.createdDateRange"></date-range-picker>
              </el-form-item>
              <el-form-item :label="$t('goodsOrderShipList.expressCodes')" >
                <el-input type="textarea" v-model="formData.expressCodes" :placeholder="$t('goodsOrderShipList.multiEnterOrComma')"></el-input>
              </el-form-item>
              <el-form-item :label="$t('goodsOrderShipList.businessId')" >
                <el-input type="textarea" v-model="formData.businessIds" :placeholder="$t('goodsOrderShipList.multiEnterOrComma')"></el-input>
              </el-form-item>
              <el-form-item :label="$t('goodsOrderShipList.status')" >
                <el-select v-model="formData.status" clearable filterable :placeholder="$t('goodsOrderShipList.selectStatus')">
                  <el-option v-for="status in formData.extra.statusList" :key="status"  :label="status" :value="status"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item :label="$t('goodsOrderShipList.remarks')" >
                <el-input v-model="formData.remarks" :placeholder="$t('goodsOrderShipList.likeSearch')" ></el-input>
              </el-form-item>
              <el-form-item :label="$t('goodsOrderShipList.expressCode')" >
                <el-input v-model="formData.expressCode" :placeholder="$t('goodsOrderShipList.likeSearch')" ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="search()">{{ $t('goodsOrderShipList.sure') }}</el-button>
        </div>
      </search-dialog>

      <el-table :data="page.content" :height="pageHeight" style="margin-top:5px;" v-loading="pageLoading" :element-loading-text="$t('goodsOrderShipList.loading')" @sort-change="sortChange" stripe border >
        <el-table-column column-key="id" prop="businessId" :label="$t('goodsOrderShipList.businessId')" sortable></el-table-column>
        <el-table-column prop="createdDate" sortable :label="$t('goodsOrderShipList.createdDate')"></el-table-column>
        <el-table-column prop="status" :label="$t('goodsOrderShipList.status')"></el-table-column>
        <el-table-column prop="shopName" :label="$t('goodsOrderShipList.shop')" width="160"></el-table-column>
        <el-table-column prop="shipType" :label="$t('goodsOrderShipList.shipType')"></el-table-column>
        <el-table-column prop="amount" :label="$t('goodsOrderShipList.amount')" :formatter="moneyFormatter"></el-table-column>
        <el-table-column prop="storeName" :label="$t('goodsOrderShipList.store')" ></el-table-column>
        <el-table-column prop="remarks" :label="$t('goodsOrderShipList.remarks')" width="200"></el-table-column>
        <el-table-column prop="netType" :label="$t('goodsOrderShipList.netType')" ></el-table-column>
        <el-table-column prop="expressOrderExpressCodes" :label="$t('goodsOrderShipList.expressCodes')" ></el-table-column>
        <el-table-column prop="pullStatus" :label="$t('goodsOrderShipList.pullStatus')" ></el-table-column>
        <el-table-column :label="$t('goodsOrderShipList.operate')" width="160">
          <template scope="scope">
            <div class="action"><el-button size="small" v-permit="'crm:goodsOrder:view'" @click.native="itemAction(scope.row.id, 'detail')">{{$t('goodsOrderShipList.detail')}}</el-button></div>
            <div class="action"  v-if="scope.row.enabled && scope.row.status=='待发货'" v-permit="'crm:goodsOrderShip:ship'" ><el-button size="small" @click.native="itemShip(scope.row.businessId)">{{$t('goodsOrderShipList.ship')}}</el-button></div>
            <div class="action"  v-if="scope.row.enabled && (scope.row.status=='待签收')" v-permit="'crm:goodsOrderShip:sign'"><el-button   size="small" @click.native="itemAction(scope.row.id, 'sign')">{{$t('goodsOrderShipList.sign')}}</el-button></div>
            <div class="action"  v-if="scope.row.enabled && (scope.row.status=='待签收')" v-permit="'crm:goodsOrderShip:shipBack'"><el-button   size="small" @click.native="itemAction(scope.row.id, 'shipBack')">{{$t('goodsOrderShipList.shipBack')}}</el-button></div>
            <div class="action"  v-if="scope.row.enabled && (scope.row.status=='待发货' || scope.row.status=='待签收')" v-permit="'crm:goodsOrderShip:mallOrder'"><el-button   size="small" @click.native="itemAction(scope.row.id, 'mallOrder')">{{$t('goodsOrderShipList.mallOrder')}}</el-button></div>
            <div class="action"  v-if="scope.row.enabled && (scope.row.status=='待发货')" v-permit="'crm:goodsOrderShip:sreturn'" ><el-button   size="small" @click.native="itemAction(scope.row.id, 'sreturn')">{{$t('goodsOrderShipList.sreturn')}}</el-button></div>
            <div class="action"  v-if="scope.row.enabled && (scope.row.status=='待发货')" v-permit="'crm:goodsOrderShip:print'"><el-button :style="scope.row.print ? '' : 'color:#ff0000;' " size="small" @click.native="itemAction(scope.row.id, 'print')">出库单</el-button></div>
            <div class="action"  v-if="scope.row.enabled && (scope.row.status=='待发货')" v-permit="'crm:goodsOrderShip:print'"><el-button :style="scope.row.shipPrint ? '' : 'color:#ff0000;' " size="small" @click.native="itemAction(scope.row.id, 'shipPrint')">快递单</el-button></div>
          </template>
        </el-table-column>
      </el-table>
      <pageable :page="page" v-on:pageChange="pageChange"></pageable>
    </div>
  </div>
</template>

<script>
  import officeSelect from 'components/basic/office-select'
  import depotSelect from 'components/future/depot-select'
  import accountSelect from 'components/basic/account-select'
  import productSelect from 'components/future/product-select'
  export default{
    components:{
      officeSelect,
      depotSelect,
      accountSelect,
      productSelect
    },
    data() {
    return {
      page:{},
      searchText:"",
      initPromise:{},
      formData:{
          extra:{}
      },
      formLabelWidth: '25%',
      formVisible: false,
      pageLoading: false
    };
  },
  methods: {
    setSearchText() {
      this.$nextTick(function () {
        this.searchText = util.getSearchText(this.$refs.searchDialog);
      })
    },
    pageRequest() {
      this.pageLoading = true;
      this.setSearchText();
      let submitData = util.deleteExtra(this.formData);
      axios.get('/api/ws/future/crm/goodsOrderShip?'+qs.stringify(submitData)).then((response) => {
        this.page = response.data;
        this.pageLoading = false;
      })
    },pageChange(pageNumber,pageSize) {
      this.formData.page = pageNumber;
      this.formData.size = pageSize;
      this.pageRequest();
    },sortChange(column) {
      console.log(column);
      this.formData.sort=util.getSort(column);
      this.formData.page=0;
      this.pageRequest();
    },search() {
      this.formVisible = false;
      this.pageRequest();
    },itemAction:function(id,action){
      if(action==="detail") {
        this.$router.push({ name: 'goodsOrderDetail', query: { id: id }})

      }else if(action==="sign"){
        util.confirmBefore(this).then(() => {
          axios.get('/api/ws/future/crm/goodsOrderShip/sign',{params:{goodsOrderId:id}}).then((response) =>{
            this.$message(response.data.message);
            this.pageRequest();
          });
        }).catch(()=>{});
      }else if(action ==="shipBack"){
        util.confirmBefore(this).then(() => {
          axios.get('/api/ws/future/crm/goodsOrderShip/shipBack', {params: {goodsOrderId: id}}).then((response) => {
            this.$message(response.data.message);
            this.pageRequest();
          });
        })
      }else if(action === "mallOrder"){
        this.$router.push({name:'goodsOrderDetail',query:{id:id,carrierEdit:true}})
      }else if(action ==="sreturn"){
        this.$router.push({name:'goodsOrderSreturn',query:{id:id}})
      }else if(action==="print"){
        window.open("/future/#/crm/goodsOrderPrint?id="+id);
      }else if(action==="shipPrint"){
        window.open("/future/#/crm/goodsOrderShipPrint?id="+id);
      }
    },itemShip(businessId){
      this.$router.push({name:'goodsOrderShip',query:{businessId:businessId}});
    },moneyFormatter(row,col){
      return util.moneyFormatter(row,col)
    }
 },created () {
     this.pageHeight = 0.74*window.innerHeight;
    this.initPromise=axios.get('/api/ws/future/crm/goodsOrderShip/getQuery').then((response) =>{
      this.formData=response.data;
    });
 },activated(){
    this.initPromise.then(()=>{
      this.pageRequest();
    });
 }
};
</script>

