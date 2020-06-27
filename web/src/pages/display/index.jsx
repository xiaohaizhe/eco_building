import React from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import {UpOutlined } from '@ant-design/icons';
import AMap from 'AMap';
import Loca from 'Loca'
import { Radio,Popover } from 'antd';
import ItemSelect from './components/ItemSelect';
import  './index.less';
var infoWin;
const content = (
  <div>
    <p>Content</p>
    <p>Content</p>
  </div>
);
const type = [{"name":"水","value":'0'},{"name":"电","value":'1'},{"name":"汽","value":'2'}];
class Display extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      show:false,
      radio:'1'
    };

  }

  componentDidMount() {
    this.loadMap()
  }

  //打开详情浮窗
  openInfoWin(map, event,title,address, content) {
    var tableDom;
    if (!infoWin) {
        infoWin = new AMap.InfoWindow({
          autoMove:false,
            isCustom: true,  //使用自定义窗体
            offset: new AMap.Pixel(170, 100)
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
        tableDom = document.createElement('table');
        infoDom.appendChild(closeDom);
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
    
    //关闭浮窗
    function closeInfoWin() {
      if (infoWin) {
          infoWin.close();
      }
    }
  }
  //生成地图
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
          '#FF5722',
          '#ffeb3b',
          '#4caf50',
          '#03a9f4',
          '#598dc0',
          '#313695'
      ];
      layer.on('click', function (ev) {
        // 事件类型
        var type = ev.type;
        // 当前元素的原始数据
        var rawData = ev.rawData;
        // 原始鼠标事件
        var originalEvent = ev.originalEvent;

        that.openInfoWin(map, originalEvent, rawData.title,rawData.address,{
            '建筑类型：': rawData.name,
            '最近一年单位面积电耗：': rawData.name,
            '最近一年单位面积水耗：': rawData.name,
            '最近一年单位面积气耗：': rawData.name,
            '节能标准：': rawData.name,
            '是否经过节能改造：': rawData.name,
            '绿建等级：': rawData.name,
            '供冷方式：': rawData.name,
            '供暖方式：': rawData.name,
            '可再生能源利用：': rawData.name
          });
      });

      // layer.on('mouseleave', function (ev) {
      //     that.closeInfoWin();
      // });
      
      var data = [
      {
        "lnglat":[116.258446,37.686622],
        "title":'xxx项目',
        "name":"景县",
        "style":2,
        'value':500
      },
      {
        "lnglat":[113.559954,22.124049],
        "name":"圣方济各堂区",
        "title":'xxx项目',
        'address':'江苏省南京市玄武区孝陵卫街道中山门大街200号',
        'value':760
      },
      {
        "lnglat":[116.366794,39.915309],
        "name":"西城区",
        "title":'xxx项目',
        'address':'江苏省南京市玄武区孝陵卫街道中山门大街200号',
        'value':880
      },
      {
        "lnglat":[116.6,39.92],
        "name":"aa区",
        "title":'xxx项目',
        'address':'江苏省南京市玄武区孝陵卫街道中山门大街200号',
        'value':650
      },
      {
        "lnglat":[116.5,39.92],
        "name":"bb区",
        "title":'xxx项目',
        'address':'江苏省南京市玄武区孝陵卫街道中山门大街200号',
        'value':550
      },
      {
        "lnglat":[115.9,39.89],
        "name":"cc区",
        "title":'xxx项目',
        'address':'江苏省南京市玄武区孝陵卫街道中山门大街200号',
        'value':440
      },
      {
        "lnglat":[115.895,39.905],
        "name":"dd区",
        "title":'xxx项目',
        'address':'江苏省南京市玄武区孝陵卫街道中山门大街200号',
        'value':220
      },
      {
        "lnglat":[116.91,42],
        "name":"ff区",
        "title":'xxx项目',
        'address':'江苏省南京市玄武区孝陵卫街道中山门大街200号',
        'value':260
      },
      {
        "lnglat":[116.123,39.87],
        "name":"ee区",
        "title":'xxx项目',
        'address':'江苏省南京市玄武区孝陵卫街道中山门大街200号',
        'value':100
      }]
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
  toggleForm(e){
    if(e == this.state.radio){
      this.setState({"show":!this.state.show})
    }else{
      this.setState({"show":true,"radio":e})
    }
    
  }
  render(){
    return (
      <PageHeaderWrapper>
        <div id='container' style={{height:'700px',width:'100%',postion:'relative'}}>
          <div className="type" >
            <ul className="ant-radio-group ant-radio-group-solid">
              {type.map((item, index) => {
                    return <li key={item.value} className={`ant-radio-button-wrapper ${this.state.radio==item.value?'ant-radio-button-wrapper-checked':''}`} onClick={()=>this.toggleForm(item.value)}>{item.name}</li>
                })}
            </ul>
            {this.state.show && <ItemSelect></ItemSelect>}
          </div>
        </div>
      </PageHeaderWrapper>)
    }
}

export default Display