import React from 'react';
import AMap from 'AMap';
import Loca from 'Loca'
import { connect } from 'umi';
import { Card } from 'antd';

var infoWin;
const energySavingStandard = ['不执行节能标准','50%','65%','75%以上','未知'];
const energySavingTransformationOrNot = ['是','否','未知'];
const gbes = ['0星','1星','2星','3星','未知'];
const coolingMode = ['集中供冷','分户供冷','无供冷','未知'];
const heatingMode = ['集中供暖', '分户采暖',  '无采暖', '未知'];
const whetherToUseRenewableResources =['否','浅层地热能', '太阳能', '未知'];
class Map extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
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
  openInfoWin(map, event,title,address, content) {
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
  loadMap = (mapData) => {
    const {id,latitude,longitude} = this.props.detail;
    let that = this;
    var map = new AMap.Map('container', {
          center: [longitude?longitude:108.5525, latitude?latitude:34.3227],
          mapStyle:'amap://styles/macaron',
          rotation: 0,
          zoom:14,
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
            '节能标准：': rawData.energySavingStandard?energySavingStandard[rawData.energySavingStandard]:'无',
            '是否经过节能改造：': rawData.energySavingTransformationOrNot?energySavingTransformationOrNot[rawData.energySavingTransformationOrNot]:'无',
            '绿建等级：': rawData.gbes?gbes[rawData.gbes]:'无',
            '供冷方式：': rawData.coolingMode?coolingMode[rawData.coolingMode]:'无',
            '供暖方式：': rawData.heatingMode?heatingMode[rawData.heatingMode]:'无',
            '可再生能源利用：': rawData.whetherToUseRenewableResources?whetherToUseRenewableResources[rawData.whetherToUseRenewableResources]:'无',
          });
      });

      //设置数据源
      layer.setData(mapData, {
        lnglat:function (obj) {
          var value = obj.value;
          console.log(isNaN(value['longitude'])+':'+value['name'])
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
                if(obj.value.id == id){
                  return 'red'
                }else{
                  return 'blue'
                }
            },
            opacity: 1
        }
    });

    layer.render();
  }
  render(){
    return (
      <div>
        <Card
            // loading={loading}
            bordered={false}
            bodyStyle={{
            padding: 0,
            }}>
          <div id='container' style={{height: `${window.innerHeight-400}px`,width:'100%'}}></div>
        </Card>
      </div>)
    }
}

export default connect(({ display, loading ,projectManage}) => ({
  display,
  detail:projectManage.detail,
  loading: loading.models.display,
}))(Map);