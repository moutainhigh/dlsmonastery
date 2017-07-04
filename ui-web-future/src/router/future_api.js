const carrierOrderList= r => require.ensure([], () => r(require('pages/future/api/carrierOrderList.vue')));
const carrierOrderForm= r => require.ensure([], () => r(require('pages/future/api/carrierOrderForm.vue')));
const carrierOrderShip= r => require.ensure([], () => r(require('pages/future/api/carrierOrderShip.vue')));
const carrierOrderEditForm= r => require.ensure([], () => r(require('pages/future/api/carrierOrderEditForm.vue')));


let routes = [
  {path:'/future/api/carrierOrderList',component:carrierOrderList,name:'carrierOrderList'},
  {path:'/future/api/carrierOrderForm',component:carrierOrderForm,name:'carrierOrderForm'},
  {path:'/future/api/carrierOrderShip',component:carrierOrderShip,name:'carrierOrderShip'},
  {path:'/future/api/carrierOrderEditForm',component:carrierOrderEditForm,name:'carrierOrderEditForm'},
]

export default routes;
