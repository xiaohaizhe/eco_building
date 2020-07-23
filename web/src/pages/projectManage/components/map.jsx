import React from 'react';
import AMap from 'AMap';
import { connect } from 'umi';


var map ,marker
class Map extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
    };
  }

  componentDidMount() {
      const {longitude,latitude} = this.props.detail;
      if(longitude&&latitude){
        this.loadMap([longitude,latitude]);
        this.addMarker([longitude,latitude]);
      }else{
        this.loadMap([116.397428, 39.90923]);
      }
    
  }
  
  componentDidUpdate(preProps) {
    const {longitude,latitude} = this.props.detail;
    if (preProps && (JSON.stringify(preProps.detail.longitude) !== JSON.stringify(this.props.detail.longitude) 
    || JSON.stringify(preProps.detail.latitude) !== JSON.stringify(this.props.detail.latitude))) {
        this.clearMarker();
        this.addMarker([longitude,latitude]);
    }
  }

    loadMap = (data) =>{
        const { dispatch } =this.props;
        // var marker, map;
        map = new AMap.Map("container", {
            resizeEnable: true,
            center: data,
            zoom: 13
        });
        map.on('click', function(e) {
            dispatch({
                type: 'projectManage/changeLongLat',
                payload:{longitude:e.lnglat.getLng(),latitude:e.lnglat.getLat()}
              });
        });
    }
    addMarker = (data)=> {
        map.setCenter(data); 
        marker = new AMap.Marker({
            position: data,
            // offset: new AMap.Pixel(-13, -30)
        });
        marker.setMap(map);
    }
    clearMarker() {
        if (marker) {
            marker.setMap(null);
            marker = null;
        }
    }
    

  render(){
    return (
      <div>
          <div id='container' style={{height: '300px',width:'100%'}}></div>
      </div>)
    }
}

export default connect(({loading ,projectManage}) => ({
    detail:projectManage.detail,
    loading: loading.models.display,
  }))(Map);