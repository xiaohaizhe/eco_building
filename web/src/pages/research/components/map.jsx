import React from 'react';
import AMap from 'AMap';
import Loca from 'Loca'
import { Card } from 'antd';
import { connect ,history} from 'umi';
import bounds from './jiangyin.json' //https://lbs.amap.com/api/javascript-api/example/marker/labelsmarker-text/?sug_index=0
import { toJSONSchema } from 'mockjs';
import '../index.less'

class Map extends React.Component {
    constructor(props) {
      super(props);
      this.state = {
      };
  
    }
  
    componentDidMount() {
        const { dispatch,research } = this.props;
        if (dispatch) {
            dispatch({
                type: 'research/filterData'
            });
        }
        this.loadMap()
    }
    componentDidUpdate(preProps) {
        const { research } = this.props;
        if (preProps && JSON.stringify(preProps.research) !== JSON.stringify(research)) {
            this.loadMap()
        }
      }
    toDetail(id){
        history.push({
            pathname: `/research/detail/${id}`,
          });
    }
    loadMap(){
        let that = this;
        const { research } = this.props;
        const {mapData,cityData}=research;
        var map = new AMap.Map('container', {
            center:[120.292838,31.886763],
            mapStyle:'amap://styles/macaron',
            rotation: 0,
            zoom: 11,
            pitch: 0,
            skyColor: '#33216a'
        });
        //添加点
        mapData.forEach(value => {
            let marker = new AMap.Marker({
                value,
                map: map,
                position: new AMap.LngLat(value['longitude']?value['longitude']:0, value['latitude']?value['latitude']:0),   // 经纬度对象，也可以是经纬度构成的一维数组[116.39, 39.9]
              });
              marker.setTitle(value.name);
              marker.on('click', function (ev) {
                // 当前元素的原始数据
                var rawData = ev.target.Ce.value;
                that.toDetail(rawData.id);
        })
    })
        
    }
    render(){
        return (
            <Card
                // loading={loading}
                title="项目图上速览"
                bordered={false}
                bodyStyle={{
                padding: 0,
                }}
            >
                <div id="container" className="map" tabIndex="0" style={{height: `${window.innerHeight-255}px`}}></div>
            </Card>)
        }
}
export default connect(({ research, loading }) => ({
    research,
    loading: loading.models.research,
  }))(Map);