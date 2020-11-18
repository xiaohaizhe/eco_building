import React from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import {DownOutlined,UpOutlined } from '@ant-design/icons';
import AMap from 'AMap';
import Loca from 'Loca'
import { Radio } from 'antd';
import { connect ,history} from 'umi';
import ItemSelect from './components/ItemSelect';
import  './index.less';
var infoWin;
const energySavingStandard = ['不执行节能标准','50%','65%','75%以上','未知'];
const energySavingTransformationOrNot = ['是','否','未知'];
const gbes = ['0星','1星','2星','3星','未知'];
const coolingMode = ['集中供冷','分户供冷','无供冷','未知'];
const heatingMode = ['集中供暖', '分户采暖',  '无采暖', '未知'];
const whetherToUseRenewableResources =['否','浅层地热能', '太阳能', '未知'];
const colorStandard = {
  "商场":{
    max:200,
    mid:170
  },
  "酒店":{
    max:200,
    mid:150
  },
  "办公":{
    max:110,
    mid:80
  },
  "医院":{
    max:300,
    mid:200
  },
  "餐饮":{
    max:200,
    mid:150
  },
  "文化教育":{
    max:110,
    mid:80
  },
  "其他":{
    max:200,
    mid:150
  }
}
class display extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      show:false,
      radio:'1',
      
    };

  }

  componentDidMount() {
    const { dispatch,display} = this.props;
    const { mapData } = display;
    if (dispatch) {
      dispatch({
        type: 'display/getMap',
      });
    }
    this.loadMap(mapData);
  }
  
  componentDidUpdate(preProps) {
    const { display } = this.props;
    const { mapData } = display;
    if (preProps && JSON.stringify(preProps.display) !== JSON.stringify(display)) {
        this.loadMap(mapData);
    }
  }
  //打开详情浮窗
  openInfoWin(map, event,title,address, content,id) {
    var tableDom;
    if (!infoWin) {
        infoWin = new AMap.InfoWindow({
          autoMove:false,
            isCustom: true,  //使用自定义窗体
            offset: new AMap.Pixel(170, 200)
        });
    }

    var x = event.x+30;
    var y = event.y+120;
    var lngLat = map.containerToLngLat(new AMap.Pixel(x, y));
    if (!tableDom) {
        let infoDom = document.createElement('div');
        infoDom.className = 'info';
        let closeDom = document.createElement('div');
        closeDom.className = 'close';
        closeDom.onclick = closeInfoWin;
        let infoHTML ='<p class="title">'+ title +'</p>'+
                      '<p>'+address+'</p>'
        infoDom.innerHTML = infoHTML;
        let bottonDom = document.createElement('button');
        bottonDom.className = 'ant-btn ant-btn-primary btnFix';
        bottonDom.innerHTML = '详细数据';
        bottonDom.onclick =function(){
          history.push({
            pathname: '/overview/proDetail/'+id
          })
        }
        tableDom = document.createElement('table');
        infoDom.appendChild(closeDom);
        infoDom.appendChild(tableDom);
        infoDom.appendChild(bottonDom);
        infoWin.setContent(infoDom);
        
    }

    var trStr = '';
    for (var name in content) {
        var val = content[name];
        trStr +=
            '<tr>' +
                '<td class="label">' + name + '</td>' +
                '<td>&nbsp;</td>' +
                '<td class="content">' + val + '</td>' +
            '</tr>'
    }
    
    tableDom.innerHTML = trStr;
    infoWin.open(map, lngLat);
    
    //关闭浮窗
    function closeInfoWin() {
      if (infoWin) {
          infoWin.close();
      }
    }
  }
  //生成地图
  loadMap(mapData){
    if (infoWin) {
      infoWin.close();
    }
    let that = this;
    var map = new AMap.Map('container', {
          center:[120.292838,31.886763],
          mapStyle:'amap://styles/macaron',
          rotation: 0,
          zoom: 13,
          pitch: 0,
          skyColor: '#33216a'
      });
    //添加点
    mapData.forEach(value => {
      let color = '#0000ff'
      var val = 0; //数值
      if(that.state.radio=='0'){
        val = value.waterConsumptionPerUnitArea || 0;
      }else if (that.state.radio=='1'){
        val = value.powerConsumptionPerUnitArea || 0;
      }else if(that.state.radio=='2'){
        val = value.gasConsumptionPerUnitArea || 0;
      }
      if(val != 0){
        let colorS = colorStandard[value.architecturalType];
        if(val<colorS.mid || val== colorS.mid){
          color = gradientColor(val,colorS.mid,0);
        }else if(val<colorS.max){
          color = gradientColor(val,colorS.max,colorS.mid);
        }else{
          color = '#ff0000'
        }
      }

      let content = '<?xml version="1.0" standalone="no"?><!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"><svg t="1600329129334" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="3195" xmlns:xlink="http://www.w3.org/1999/xlink" width="30" height="30"><defs><style type="text/css"></style></defs><path d="M512 0C345.975467 0 152.234667 101.444267 152.234667 359.765333c0 175.3088 276.753067 562.7904 359.765333 664.234667 73.796267-101.444267 359.765333-479.709867 359.765333-664.234667C871.765333 101.512533 678.024533 0 512 0z" fill='+color+' p-id="3196"></path></svg>'
      let marker = new AMap.Marker({
        value,
        map: map,
        content:content,
        position: new AMap.LngLat(value['longitude']?value['longitude']:0, value['latitude']?value['latitude']:0),   // 经纬度对象，也可以是经纬度构成的一维数组[116.39, 39.9]
      });
      marker.on('click', function (ev) {
            // 事件类型
            var type = ev.type;
            // 当前元素的原始数据
            var rawData = ev.target.Ce.value;
            // 原始鼠标事件
            var originalEvent = ev.pixel;
            
            that.openInfoWin(map, originalEvent, rawData.name,rawData.address,{
                '建筑类型：': rawData.architecturalType || '无',
                '最近一年单位面积电耗：': (rawData.powerConsumptionPerUnitArea || 0 ).toFixed(3)+' kWh/㎡',
                '最近一年单位面积水耗：': (rawData.waterConsumptionPerUnitArea || 0).toFixed(3)+' m³/㎡',
                '最近一年单位面积汽耗：': (rawData.gasConsumptionPerUnitArea || 0).toFixed(3)+' m³/㎡',
                '节能标准：': rawData.energySavingStandard!=undefined?energySavingStandard[rawData.energySavingStandard]:'无',
                '是否经过节能改造：': rawData.energySavingTransformationOrNot!=undefined?energySavingTransformationOrNot[rawData.energySavingTransformationOrNot]:'无',
                '绿建等级：': rawData.gbes!=undefined?gbes[rawData.gbes]:'无',
                '供冷方式：': rawData.coolingMode!=undefined?coolingMode[rawData.coolingMode]:'无',
                '供暖方式：': rawData.heatingMode!=undefined?heatingMode[rawData.heatingMode]:'无',
                '可再生能源利用：': rawData.whetherToUseRenewableResources!=undefined?whetherToUseRenewableResources[rawData.whetherToUseRenewableResources]:'无',
              },rawData.id);
          });
          function gradientColor(data,max,min){
            debugger
            let startR = 0;let startG = 0;let startB = 0;let endR = 0;let endG = 0;let endB = 0;
              if(min==0){
                //左边
                startR = 255;
                startG = 255;
                startB = 0;
               
                endR = 0;
                endG = 112;
                endB = 192;
              }else{
                //右边
                startR = 255;
                startG = 0;
                startB = 0;
               
                endR = 255;
                endG = 255;
                endB = 0;
              }
                
               
                let step = (max-data)/(max-min);
                let sR = (endR-startR)*step;//总差值
                let sG = (endG-startG)*step;
                let sB = (endB-startB)*step;
               
                var color = 'rgb('+parseInt((sR+startR))+','+parseInt((sG+startG))+','+parseInt((sB+startB))+')';
                return color;
          }
    })
    
    //   var layer = new Loca.PointLayer({
    //       map: map,
    //       eventSupport: true
    //   });

    //   layer.on('click', function (ev) {
        
    //     // 事件类型
    //     var type = ev.type;
    //     // 当前元素的原始数据
    //     var rawData = ev.rawData;
    //     // 原始鼠标事件
    //     var originalEvent = ev.originalEvent;
        
    //     that.openInfoWin(map, originalEvent, rawData.name,rawData.address,{
    //         '建筑类型：': rawData.architecturalType || '无',
    //         '最近一年单位面积电耗：': (rawData.powerConsumptionPerUnitArea || 0 ).toFixed(2)+' kWh/㎡',
    //         '最近一年单位面积水耗：': (rawData.waterConsumptionPerUnitArea || 0).toFixed(2)+' m³/㎡',
    //         '最近一年单位面积汽耗：': (rawData.gasConsumptionPerUnitArea || 0).toFixed(2)+' m³/㎡',
    //         '节能标准：': rawData.energySavingStandard!=undefined?energySavingStandard[rawData.energySavingStandard]:'无',
    //         '是否经过节能改造：': rawData.energySavingTransformationOrNot!=undefined?energySavingTransformationOrNot[rawData.energySavingTransformationOrNot]:'无',
    //         '绿建等级：': rawData.gbes!=undefined?gbes[rawData.gbes]:'无',
    //         '供冷方式：': rawData.coolingMode!=undefined?coolingMode[rawData.coolingMode]:'无',
    //         '供暖方式：': rawData.heatingMode!=undefined?heatingMode[rawData.heatingMode]:'无',
    //         '可再生能源利用：': rawData.whetherToUseRenewableResources!=undefined?whetherToUseRenewableResources[rawData.whetherToUseRenewableResources]:'无',
    //       },rawData.id);
    //   });

    //   // layer.on('mouseleave', function (ev) {
    //   //     that.closeInfoWin();
    //   // });
      
    //   // var data = [
    //   // {
    //   //   "lnglat":[116.258446,37.686622],
    //   //   "title":'xxx项目',
    //   //   "name":"景县",
    //   //   "style":2,
    //   //   'value':500
    //   // }]
    //   //设置数据源
    //   layer.setData(mapData, {
    //     lnglat:function (obj) {
    //       var value = obj.value;
    //       // console.log(isNaN(value['longitude'])+':'+value['name'])
    //       return [value['longitude']?value['longitude']:0, value['latitude']?value['latitude']:0];
    //     },
    //     type:'json'// 指定坐标数据的来源，数据格式: 经度在前，维度在后，数组格式。
    //   });
    //   //设置参数
    //   layer.setOptions({
    //     unit: 'px',
    //     style: {
    //         radius: 10,
    //         height: 0,
    //         // 根据车辆类型设定不同填充颜色
    //         color: function (obj) {
    //             if(typeData.max-typeData.min===0){
    //               return '#0000ff';
    //             }else{
    //               var value = 0;
    //               if(that.state.radio=='0'){
    //                 value = obj.value.waterConsumptionPerUnitArea || 0;
    //               }else if (that.state.radio=='1'){
    //                 value = obj.value.powerConsumptionPerUnitArea || 0;
    //               }else if(that.state.radio=='2'){
    //                 value = obj.value.gasConsumptionPerUnitArea || 0;
    //               }
    //               var max = typeData.max;
    //               var min = typeData.min;
                  
    //               return gradientColor(value,max,min);
    //             }
                

    //             function gradientColor(data,max,min){
    //               //   let startRGB = this.colorRgb('#ff0000');//转换为rgb数组模式
    //                 let startR = 194;
    //                 let startG = 107;
    //                 let startB = 80;
    //                
    //               //   let endRGB = this.colorRgb('#0000ff'); 
    //                 let endR = 81;
    //                 let endG = 153;
    //                 let endB = 133;
    //                
    //                 let step = (max-data)/(max-min);
    //                 let sR = (endR-startR)*step;//总差值
    //                 let sG = (endG-startG)*step;
    //                 let sB = (endB-startB)*step;
    //                
    //                 var color = 'rgb('+parseInt((sR+startR))+','+parseInt((sG+startG))+','+parseInt((sB+startB))+')';
    //                 return color;
    //                }
    //         },
    //         opacity: 1
    //     }
    // });

    // layer.render();
  }
  toggleForm(){
      this.setState({"show":!this.state.show})
  }
  toggleRadio(e){
    const { display } = this.props;
    const { mapData,maxMin } = display;
    this.setState({
      radio: e.target.value,
    });
    this.loadMap(mapData,maxMin[e.target.value]);
  };
  render(){
    const { display } = this.props;
    const { maxMin,height } = display;
    return (
      <PageHeaderWrapper title={false}>
        <div className="display">
          <div id='container' style={{height:`${height}px`,width:'100%'}}></div>
          <div className="type" >
            <Radio.Group value={this.state.radio} buttonStyle="solid" onChange={(e)=>this.toggleRadio(e)}>
              <Radio.Button value="0">水</Radio.Button>
              <Radio.Button value="1">电</Radio.Button>
              <Radio.Button value="2">汽</Radio.Button>
            </Radio.Group>
            <div className="ant-radio-button-wrapper" onClick={()=>this.toggleForm()}>
              {this.state.show?<UpOutlined />:<DownOutlined />}
            </div>
            {this.state.show && <ItemSelect></ItemSelect>}
          </div>
          <div className="legend">
            <div className="gradientLegend"></div>
            <p className="max">约束值</p>
            <p className="mid">引导值</p>
            <p className="min">0</p>
          </div>
        </div>
      </PageHeaderWrapper>)
    }
}

export default connect(({ display, loading }) => ({
  display,
  loading: loading.models.display,
}))(display);