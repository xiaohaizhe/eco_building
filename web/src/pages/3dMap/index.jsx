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
var polygon;
var map;
const energySavingStandard = ['不执行节能标准','50%','65%','75%以上','未知'];
const energySavingTransformationOrNot = ['是','否','未知'];
const gbes = ['0星','1星','2星','3星','未知'];
const coolingMode = ['集中供冷','分户供冷','无供冷','未知'];
const heatingMode = ['集中供暖', '分户采暖',  '无采暖', '未知'];
const whetherToUseRenewableResources =['否','浅层地热能', '太阳能', '未知'];
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
    const { mapData,maxMin } = display;
    if (dispatch) {
      dispatch({
        type: 'display/getMap',
      });
    }
    
    this.loadMap(mapData,maxMin[this.state.radio],this.state.radio);
  }
  
  componentDidUpdate(preProps) {
    const { display } = this.props;
    const { mapData,maxMin } = display;
    if (preProps && JSON.stringify(preProps.display) !== JSON.stringify(display)) {
        this.loadMap(mapData,maxMin[this.state.radio],this.state.radio);
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

    var x = event.x;
    var y = event.y;
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
          map.remove(polygon);
      }
    }
  }
  //生成地图
  loadMap(mapData,typeData,flag){
    if (infoWin) {
      infoWin.close();
    }
    let that = this;
    map = new AMap.Map("container", {
        zoom:17,
        pitch:50,
        mapStyle:'amap://styles/light',
        center:[120.2794519,31.91857754],
        features:['bg','point','road'],
        viewMode:'3D',
    });
    var buildingLayer = new AMap.Buildings({zIndex:50,merge:false,sort:false,map:map});
    let areas = [];
    mapData.forEach(value => {
      if(value.shape && value.shape.length>1){
        debugger
        let color2 = 'ff414141';
        let color1 = 'ff969696';
      if(typeData.max-typeData.min!=0){
        var val = 0;
        if(flag==='0'){
          val = value.waterConsumptionPerUnitArea;
        }else if (flag==='1'){
          val = value.powerConsumptionPerUnitArea;
        }else if(flag==='2'){
          val = value.gasConsumptionPerUnitArea;
        }
        var max = typeData.max;
        var min = typeData.min;
        if(val || val===0){
          color2 = gradientColor(val,max,min,true);
          color1 = gradientColor(val,max,min,false);
        }
        
      }

        areas.push({path:value.shape,color1: color1,//楼顶颜色
        color2: color2,//楼面颜色
      })
      }
      
      //添加点标记
      let marker = new AMap.Marker({
        value,
        map: map,
        position: new AMap.LngLat(value['longitude']?value['longitude']:0, value['latitude']?value['latitude']:0),   // 经纬度对象，也可以是经纬度构成的一维数组[116.39, 39.9]
      });
      marker.on('click', function (ev) {
            if(polygon){
              map.remove(polygon);
            }
            
            // 事件类型
            var type = ev.type;
            // 当前元素的原始数据
            var rawData = ev.target.Ce.value;
            // 原始鼠标事件
            var originalEvent = ev.pixel;
            if(rawData.shape && rawData.shape.length>1){
              polygon = new AMap.Polygon({
                bubble:false,
                fillOpacity:0.3,
                strokeWeight:0.1,
                path:rawData.shape,
                map:map,
                zIndex:1
              })
            }
            
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
    })
    let options = 
    {
         hideWithoutStyle:false,//是否隐藏设定区域外的楼块
         areas:areas
    };
    buildingLayer.setStyle(options); //此配色优先级高于自定义mapStyle
    function gradientColor(data,max,min,flag){
      //   let startRGB = this.colorRgb('#ff0000');//转换为rgb数组模式
        let startR = 194;
        let startG = 107;
        let startB = 80;
       
      //   let endRGB = this.colorRgb('#0000ff'); 
        let endR = 81;
        let endG = 153;
        let endB = 133;
       
        let step = (max-data)/(max-min);
        let sR = (endR-startR)*step;//总差值
        let sG = (endG-startG)*step;
        let sB = (endB-startB)*step;
       
      //   var color = 'rgb('+parseInt((sR+startR))+','+parseInt((sG+startG))+','+parseInt((sB+startB))+')';
        var color
        if(flag){
          color = 'ff'+ parseInt((sR+startR)).toString(16)+ parseInt((sG+startG)).toString(16)+parseInt((sB+startB)).toString(16)
        }else{
          color = 'ff'+ parseInt((sR+startR+40)).toString(16)+ parseInt((sG+startG+40)).toString(16)+parseInt((sB+startB+40)).toString(16)
        }
        
        return color;
    }
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
    this.loadMap(mapData,maxMin[e.target.value],e.target.value);
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
            <p className="max">{Math.ceil(maxMin[this.state.radio].max)}</p>
            <p className="min">{Math.floor(maxMin[this.state.radio].min)}</p>
          </div>
        </div>
      </PageHeaderWrapper>)
    }
}

export default connect(({ display, loading }) => ({
  display,
  loading: loading.models.display,
}))(display);