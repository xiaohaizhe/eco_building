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
    this.loadMap(mapData,maxMin[this.state.radio]);
  }
  
  componentDidUpdate(preProps) {
    const { display } = this.props;
    const { mapData,maxMin } = display;
    if (preProps && JSON.stringify(preProps.display) !== JSON.stringify(display)) {
        this.loadMap(mapData,maxMin[this.state.radio]);
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

    var x = event.offsetX;
    var y = event.offsetY;
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
  loadMap(mapData,typeData){
    if (infoWin) {
      infoWin.close();
    }
    let that = this;
    var map = new AMap.Map('container', {
          center: [108.5525, 34.3227],
          mapStyle:'amap://styles/macaron',
          rotation: 0,
          zoom: 4.5,
          pitch: 0,
          skyColor: '#33216a'
      });

      var layer = new Loca.ScatterPointLayer({
          map: map,
          eventSupport: true
      });

      layer.on('click', function (ev) {
        
        // 事件类型
        var type = ev.type;
        // 当前元素的原始数据
        var rawData = ev.rawData;
        // 原始鼠标事件
        var originalEvent = ev.originalEvent;
        
        that.openInfoWin(map, originalEvent, rawData.name,rawData.address,{
            '建筑类型：': rawData.architecturalType || '无',
            '最近一年单位面积电耗：': (rawData.powerConsumptionPerUnitArea || 0 )+' kWh/㎡',
            '最近一年单位面积水耗：': (rawData.waterConsumptionPerUnitArea || 0)+' m³/㎡',
            '最近一年单位面积汽耗：': (rawData.gasConsumptionPerUnitArea || 0)+' m³/㎡',
            '节能标准：': rawData.energySavingStandard!=undefined?energySavingStandard[rawData.energySavingStandard]:'无',
            '是否经过节能改造：': rawData.energySavingTransformationOrNot!=undefined?energySavingTransformationOrNot[rawData.energySavingTransformationOrNot]:'无',
            '绿建等级：': rawData.gbes!=undefined?gbes[rawData.gbes]:'无',
            '供冷方式：': rawData.coolingMode!=undefined?coolingMode[rawData.coolingMode]:'无',
            '供暖方式：': rawData.heatingMode!=undefined?heatingMode[rawData.heatingMode]:'无',
            '可再生能源利用：': rawData.whetherToUseRenewableResources!=undefined?whetherToUseRenewableResources[rawData.whetherToUseRenewableResources]:'无',
          },rawData.id);
      });

      // layer.on('mouseleave', function (ev) {
      //     that.closeInfoWin();
      // });
      
      // var data = [
      // {
      //   "lnglat":[116.258446,37.686622],
      //   "title":'xxx项目',
      //   "name":"景县",
      //   "style":2,
      //   'value':500
      // }]
      //设置数据源
      layer.setData(mapData, {
        lnglat:function (obj) {
          var value = obj.value;
          // console.log(isNaN(value['longitude'])+':'+value['name'])
          return [value['longitude']?value['longitude']:0, value['latitude']?value['latitude']:0];
        },
        type:'json'// 指定坐标数据的来源，数据格式: 经度在前，维度在后，数组格式。
      });
      //设置参数
      layer.setOptions({
        unit: 'px',
        style: {
            radius: 10,
            height: 0,
            // 根据车辆类型设定不同填充颜色
            color: function (obj) {
                if(typeData.max-typeData.min===0){
                  return '#0000ff';
                }else{
                  var value = 0;
                  if(that.state.radio=='0'){
                    value = obj.value.waterConsumptionPerUnitArea || 0;
                  }else if (that.state.radio=='1'){
                    value = obj.value.powerConsumptionPerUnitArea || 0;
                  }else if(that.state.radio=='2'){
                    value = obj.value.gasConsumptionPerUnitArea || 0;
                  }
                  var max = typeData.max;
                  var min = typeData.min;
                  
                  return gradientColor(value,max,min);
                }
                

                function gradientColor(data,max,min){
                  //   let startRGB = this.colorRgb('#ff0000');//转换为rgb数组模式
                    let startR = 255;
                    let startG = 0;
                    let startB = 0;
                   
                  //   let endRGB = this.colorRgb('#0000ff'); 
                    let endR = 0;
                    let endG = 0;
                    let endB = 255;
                   
                    let step = (max-data)/(max-min);
                    let sR = (endR-startR)*step;//总差值
                    let sG = (endG-startG)*step;
                    let sB = (endB-startB)*step;
                   
                    var color = 'rgb('+parseInt((sR+startR))+','+parseInt((sG+startG))+','+parseInt((sB+startB))+')';
                    return color;
                   }
            },
            opacity: 1
        }
    });

    layer.render();
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