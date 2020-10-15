import React from 'react';
import AMap from 'AMap';
import Loca from 'Loca'
import { Card } from 'antd';
import { connect } from 'umi';
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
        const { dispatch,projectManage } = this.props;
        if (dispatch) {
            dispatch({
                type: 'projectManage/filterData'
            });
        }
        this.loadMap()
    }
    componentDidUpdate(preProps) {
        const { projectManage } = this.props;
        if (preProps && JSON.stringify(preProps.projectManage) !== JSON.stringify(projectManage)) {
            this.loadMap()
        }
      }
    // filterData(mapData){
    //     let data=[];
    //     let cities=[];
    //     for(let i=0;i<mapData.length;i++){
    //         if(cities.indexOf(mapData[i].city)<0){
    //             cities.push(mapData[i].city);
    //             data.push(
    //                 {name:mapData[i].city,lnglat:[mapData[i].longitude,mapData[i].latitude],children:[{lnglat:[mapData[i].longitude,mapData[i].latitude],name:mapData[i].name}]}
    //             );
    //         }else{
    //             console.log(JSON.stringify(data[cities.indexOf(mapData[i].city)]))
    //             data[cities.indexOf(mapData[i].city)].children.push({lnglat:[mapData[i].longitude,mapData[i].latitude],name:mapData[i].name})
    //         }
            
    //     }
    //     this.loadMap(data);
    // }
    
    loadMap(){
        const { projectManage } = this.props;
        const {mapData,cityData}=projectManage;
        var cluster, markers = [];
        var mask = []
        for(var i =0;i<bounds.length;i+=1){
            mask.push([bounds[i]])
        }
        var map = new AMap.Map("container", {
            resizeEnable: true,
            center: [120.292838,31.856763],
            zoom: 11,
            mapStyle: "amap://styles/dark"
        });
        //标注江苏省
        var polygon = new AMap.Polygon({
            strokeWeight: 1,
            path: mask,
            fillOpacity: 0.1,
            fillColor: '#80d8ff',
            strokeColor: '#26B99A'
        });
        map.add(polygon);

        // var locations = [
        //     {"lnglat":["118.78","32.04"]},{"lnglat":["113.370643","22.938827"]},{"lnglat":["112.985037","23.15046"]}
        // ];
        for (var i = 0; i < mapData.length; i ++) {
            var marker = new AMap.Marker({
                content: '<div class="circle"><span class="name">'+mapData[i].name+'</span></div>',
                title:mapData[i].name,
                city:mapData[i].city,
                district:mapData[i].district,
                // street:mapData[i].street,
                position: [mapData[i].longitude,mapData[i].latitude],
                offset: new AMap.Pixel(-24, 5),
            });
            markers.push(marker);
        }
        map.plugin(["AMap.MarkerClusterer"], function() {
            cluster = new AMap.MarkerClusterer(map, markers,{
                renderClusterMarker: _renderClusterMarker
            });
        });
        // function onZoomEnd(e){

        //     var zoomLevel=map.getZoom(); //获取地图缩放级别
        //     if(zoomLevel>7){
        //         markers=[]
        //         cluster.clearMarkers();
        //         for (var i = 0; i < mapData.length; i ++) {
        //             var marker = new AMap.Marker({
        //                 // content: div,
        //                 title:mapData[i].name,
        //                 city:mapData[i].city,
        //                 position: [mapData[i].longitude,mapData[i].latitude],
        //                 offset: new AMap.Pixel(-24, 5),
        //                 // zIndex: length,
        //             });
        //             markers.push(marker);
        //         }
                
        //     }else{
        //         markers=[];
        //         cluster.clearMarkers();
        //         for (var i = 0; i < cityData.length; i ++) {
        //             var marker = createMarker(cityData[i],cityData.children?cityData.children.length:0);
        //             markers.push(marker);
        //         }
        //     }
        //     map.plugin(["AMap.MarkerClusterer"], function() {
        //         cluster = new AMap.MarkerClusterer(map, markers, {
        //             renderClusterMarker: _renderClusterMarker
        //         });
        //     });
        // }
            

        
        //     AMap.event.addListener(map, 'zoomend', onZoomEnd);    

        function _renderClusterMarker(context) {
            // debugger
            var level = map.getZoom();
            console.log(level)
            var div = document.createElement('div');
            var bgColor = 'hsla(209,100%,55%,0.7)';
            var fontColor = '#fff';
            div.style.backgroundColor = bgColor;
            var size = Math.round(30 + Math.pow(context.count / mapData.length, 1 / 5) * 20);
            div.className='circle';
            // div.style.borderRadius = '50%';
            // div.style.lineHeight = size-7+ 'px';
            // div.style.color = fontColor;
            // div.style.fontSize = '12px';
            // div.style.textAlign = 'center';
            if(level<9){
                div.innerHTML = '<div class="name"><div>'+context.markers[0].w.district+'</div>'+
                '<div>'+context.count+'</div></div>';
            }
            // else if(level<12||level==12){
            //     div.innerHTML = '<div class="name"><div>'+context.markers[0].w.district+'</div>'
            //     +'<div>'+context.count+'</div></div>';
            // }
            else if(level<13||level==13){
                div.innerHTML = //'<div class="name"><div>'+context.markers[0].w.district+'</div>'+
                '<div>'+context.count+'</div></div>';
            }else{
                div.innerHTML = '<div class="name"><div>'+context.markers[0].w.title+'</div>'
                +'<div>'+context.count+'</div></div>';
            }
            context.marker.setOffset(new AMap.Pixel(-size / 2, -size / 2));
            context.marker.setContent(div)
        };
        // function createMarker (data,length,hide) {
        //     var div = document.createElement('div');
        //     div.className = 'circle';
        //     var r = Math.floor(length / 1024);
        //     div.style.backgroundColor ='#393'
        //     var htmlData='<div>'+data.name+'</div>'+'<div>'+length+'</div>';
        //     div.innerHTML = htmlData;
            
        //     var marker = new AMap.Marker({
        //         content: div,
        //         title:data.name,
        //         city:data.city,
        //         position: data.lnglat,
        //         offset: new AMap.Pixel(-24, 5),
        //         zIndex: length,
        //     });
        //     marker.subMarkers = [];
        //     // if(data.name==='南京市'||data.name==='苏州市'){
        //         // marker.setLabel({content:'&larr;请点击',offset:new AMap.Pixel(45,0)})
        //         // map.setCenter(marker.getPosition());
        //     // }
        //     // if(!hide){
        //     //     marker.setMap(map)
        //     // }
        //      if(data.children&&data.children.length){
        //         for(var i = 0 ; i<data.children.length;i+=1){
        //             marker.subMarkers.push(createMarker(data.children[i],data.children?data.children.length:0));
        //         }
        //     } 
        //     return marker;
        // }
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
export default connect(({ projectManage, loading }) => ({
    projectManage,
    loading: loading.models.projectManage,
  }))(Map);