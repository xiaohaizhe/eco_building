import React from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import AMap from 'AMap';
import Loca from 'Loca'

var infoWin;
var tableDom;

class Display extends React.Component {
  constructor(props) {
    super(props);
    this.state = {

    };

  }

  componentDidMount() {
    this.loadMap()
  }

  openInfoWin(map, event, content) {
    debugger
    if (!infoWin) {
        infoWin = new AMap.InfoWindow({
          autoMove:false,
            isCustom: true,  //使用自定义窗体
            offset: new AMap.Pixel(130, 100)
        });
    }

    var x = event.offsetX;
    var y = event.offsetY;
    var lngLat = map.containerToLngLat(new AMap.Pixel(x, y));

    if (!tableDom) {
        let infoDom = document.createElement('div');
        infoDom.className = 'info';
        tableDom = document.createElement('table');
        infoDom.appendChild(tableDom);
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
  }

  closeInfoWin() {
    if (infoWin) {
        infoWin.close();
    }
  }

  loadMap(){
    let that = this;
    var map = new AMap.Map('container', {
          center: [116.400389, 39.93729],
          mapStyle:'amap://styles/macaron',
          rotation: 0,
          zoom: 11.3,
          pitch: 0,
          skyColor: '#33216a'
      });

      var layer = new Loca.ScatterPointLayer({
          map: map,
          eventSupport: true
      });

      var colors = [
          '#a50026',
          '#e34a33',
          '#fca55d',
          '#fee99d',
          '#e9f6e8',
          '#a3d3e6',
          '#598dc0',
          '#313695'
      ];

      layer.on('mousemove', function (ev) {

        // 事件类型
        var type = ev.type;
        // 当前元素的原始数据
        var rawData = ev.rawData;
        // 原始鼠标事件
        var originalEvent = ev.originalEvent;

        that.openInfoWin(map, originalEvent, {
            '名称': rawData.name,
            '位置': rawData.lnglat
          });
      });

      layer.on('mouseleave', function (ev) {
          that.closeInfoWin();
      });
      
      var data = [{"lnglat":[116.258446,37.686622],"name":"景县","style":2,'value':500},{"lnglat":[113.559954,22.124049],"name":"圣方济各堂区",'value':600},{"lnglat":[116.366794,39.915309],"name":"西城区",'value':999}]
      //设置数据源
      layer.setData(data, {
        lnglat: 'lnglat'   // 指定坐标数据的来源，数据格式: 经度在前，维度在后，数组格式。
      });
      //设置参数
      layer.setOptions({
        unit: 'px',
        style: {
            radius: 6,
            height: 0,
            // 根据车辆类型设定不同填充颜色
            color: function (obj) {
                var value = obj.value.value;
                if(value>875){
                  return colors[0];
                }else if(value>750){
                  return colors[1];
                }else if(value>625){
                  return colors[2];
                }else if(value>500){
                  return colors[3];
                }else if(value>375){
                  return colors[4];
                }else if(value>250){
                  return colors[5];
                }else if(value>125){
                  return colors[6];
                }else{
                  return colors[7];
                }
            },
            opacity: 1
        }
    });

    layer.render();
  }

  render(){
    return (
      <PageHeaderWrapper>
        <div id='container' style={{height:'500px',width:'100%'}}></div>
      </PageHeaderWrapper>)
    }
}

export default Display