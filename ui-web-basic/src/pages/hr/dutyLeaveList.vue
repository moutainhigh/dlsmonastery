<template>
  <div>
    <head-tab active="dutyLeaveList"></head-tab>
    <div>
      <el-row>
        <el-button type="primary"@click="formVisible = true" icon="search">{{$t('dutyLeaveList.filter')}}</el-button>
        <span v-html="searchText"></span>
      </el-row>
      <search-dialog @enter="search()" :show="formVisible" @hide="formVisible=false" :title="$t('dutyLeaveList.filter')" v-model="formVisible" size="tiny" class="search-form" z-index="1500" ref="searchDialog">
        <el-form :model="formData"  :label-width="formLabelWidth">
              <el-form-item :label="$t('dutyLeaveList.dutyDate')" prop="dutyDate">
                <date-range-picker v-model="formData.dutyDate"></date-range-picker>
              </el-form-item>
              <el-form-item :label="$t('dutyLeaveList.leaveType')" prop="leaveDate">
                <dict-enum-select v-model="formData.leaveType" category="请假类型" />
              </el-form-item>
              <el-form-item :label="$t('dutyLeaveList.dateType')" prop="dateType">
                <el-select v-model="formData.dateType" filterable clearable :placeholder="$t('dutyLeaveList.inputKey')">
                  <el-option v-for="item in formData.extra.dateList" :key="item" :label="item" :value="item"></el-option>
                </el-select>
              </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="search()">{{$t('dutyLeaveList.sure')}}</el-button>
        </div>
      </search-dialog>
      <el-table :data="page.content" :height="pageHeight" style="margin-top:5px;" v-loading="pageLoading" :element-loading-text="$t('dutyLeaveList.loading')" @sort-change="sortChange" stripe border>
        <el-table-column prop="dutyDate" :label="$t('dutyLeaveList.dutyDate')"></el-table-column>
        <el-table-column prop="dateType"  :label="$t('dutyLeaveList.dateType')" ></el-table-column>
        <el-table-column prop="leaveType" :label="$t('dutyLeaveList.leaveType')"></el-table-column>
        <el-table-column prop="remarks" :label="$t('dutyLeaveList.remarks')"></el-table-column>
        <el-table-column prop="status"  :label="$t('dutyLeaveList.status')">
          <template scope="scope">
            <el-tag :type="scope.row.status == '申请中' ? 'primary' : 'danger'">{{scope.row.status}}</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <pageable :page="page" v-on:pageChange="pageChange"></pageable>
    </div>
  </div>
</template>
<script>
  import dictEnumSelect from 'components/basic/dict-enum-select'
  export default {
    components:{
      dictEnumSelect
    },
    data() {
      return {
        page:{},
        formData:{
          extra:{},
        },
        searchText:{},
        initPromise:{},
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
        var submitData = util.deleteExtra(this.formData);
        axios.get('/api/basic/hr/dutyLeave?'+qs.stringify(submitData)).then((response) => {
          this.page = response.data;
          this.pageLoading = false;
        })
      },pageChange(pageNumber,pageSize) {
        this.formData.page = pageNumber;
        this.formData.size = pageSize;
        this.pageRequest();
      },sortChange(column) {
        this.formData.order=util.getSort(column);
        this.formData.page=0;
        this.pageRequest();
      },search() {
        this.formVisible = false;
        this.pageRequest();
      }
    },created () {
       this.pageHeight = 0.74*window.innerHeight;
      this.initPromise = axios.get('/api/basic/hr/dutyLeave/getQuery').then((response) =>{
        this.formData = response.data;
      });
    },activated() {
        this.initPromise.then(()=>{
          this.pageRequest();
        })
    }
  };
</script>

