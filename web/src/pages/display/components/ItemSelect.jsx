import React from 'react';
import { connect } from 'umi';
import { Cascader,Form, Input, Button, Checkbox,DatePicker  } from 'antd';
const { RangePicker } = DatePicker;

const architecturalType = [
  { label: '商场', value: '商场' },
  { label: '酒店', value: '酒店' },
  { label: '办公', value: '办公' },
  { label: '医院', value: '医院' },
  { label: '餐饮', value: '餐饮' },
  { label: '文化教育', value: '文化教育' },
  { label: '其他', value: '其他' }
];
const floor = [
  { label: '1-3层（低层）', value: '1,2,3' },
  { label: '4-6层（多层）', value: '4,5,6' },
  { label: '7层以上（高层）', value: '7,8,9' },
  { label: '其他', value: '0' }
];
const gbes = [
  { label: '0星', value: '0' },
  { label: '1星', value: '1' },
  { label: '2星', value: '2' },
  { label: '3星', value: '3' },
  { label: '未知', value: '4' }
];
const energySavingStandard = [
  { label: '50%', value: '1' },
  { label: '65%', value: '2' },
  { label: '75%以上', value: '3' },
  { label: '不执行', value: '0' },
  { label: '未知', value: '4' }
];
const energySavingTransformationOrNot = [
  { label: '是', value: '0' },
  { label: '否', value: '1' },
  { label: '未知', value: '2' }
];
const coolingMode = [
  { label: '集中供冷', value: '0' },
  { label: '分户供冷', value: '1' },
  { label: '无供冷', value: '2' },
  { label: '未知', value: '3' }
];
const heatingMode = [
  { label: '集中供暖', value: '0' },
  { label: '分户采暖', value: '1' },
  { label: '无采暖', value: '2' },
  { label: '未知', value: '3' }
];
const whetherToUseRenewableResources =[
  { label: '太阳能', value: '1' },
  { label: '浅层地热', value: '2' },
  { label: '否', value: '0' },
  { label: '未知', value: '3' }
];
const layout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 18 },
};
const tailLayout = {
  wrapperCol: { offset: 8, span: 16 },
};

const onFinishFailed = errorInfo => {
  console.log('Failed:', errorInfo);
};

function onChange(value) {
  console.log(value);
}
class ItemSelect extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      other:false
    };
  
  }
  componentDidMount() {
    const { dispatch} = this.props;
    if (dispatch) {
      dispatch({
        type: 'display/getAddressOnMap',
      });
    }
  }
  //层数变化
  floorChange(value){
    if(value.indexOf('0')>-1){
      this.setState({'other':true})
    }else{
      this.setState({'other':false})
    }
  }

  onFinish (values) {
    console.log('Success:', values);
    
    let result = {
      gbes:values.gbes?values.gbes.toLocaleString():'',
      architecturalType:values.architecturalType?values.architecturalType.toLocaleString():'',
      energySavingStandard:values.energySavingStandard?values.energySavingStandard.toLocaleString():'',
      energySavingTransformationOrNot:values.energySavingTransformationOrNot?values.energySavingTransformationOrNot.toLocaleString():'',
      coolingMode:values.coolingMode?values.coolingMode.toLocaleString():'',
      heatingMode:values.heatingMode?values.heatingMode.toLocaleString():'',
      whetherToUseRenewableResources:values.whetherToUseRenewableResources?values.whetherToUseRenewableResources.toLocaleString():'',
      area:(values.areaMin?values.areaMin:0)+(values.areaMax?','+values.areaMax:''),
      date:values.date?(values.date[0].format('YYYY')+','+values.date[1].format('YYYY')):'',
      powerConsumptionPerUnitArea:(values.electricMin?values.electricMin:0)+(values.electricMax?','+values.electricMax:''),
      gasConsumptionPerUnitArea:(values.gasMin?values.gasMin:0)+(values.gasMax?','+values.gasMax:''),
      waterConsumptionPerUnitArea:(values.waterMin?values.waterMin:0)+(values.waterMax?','+values.waterMax:''),
    };
    if(this.state.other){
      let arr = [];
      for(let i = values.floorMin;i<=values.floorMax;i++){
        arr.push(i);
      }
      if(values.floor){
        let temp = values.floor.toLocaleString();
        temp = temp.substr(0,temp.length-2).split(",");
        result.floor=  Array.from(new Set(arr.concat(temp))).toLocaleString();
      }
    }else{
      result.floor = values.floor?values.floor.toLocaleString():''
    }
    if(values.address){
      if(values.address.length==4){
        result.province = values.address[0];
        result.city = values.address[1];
        result.district = values.address[2];
        result.street = values.address[3];
      }else if(values.address.length==3){
        result.province = values.address[0];
        result.city = values.address[1];
        result.district = values.address[2];
      }else if(values.address.length==2){
        result.province = values.address[0];
        result.city = values.address[1];
      }else if(values.address.length==1){
        result.province = values.address[0];
      }
    }
    
    const { dispatch } = this.props;
    if (dispatch) {
      dispatch({
        type: 'display/getParams',
        payload: result
      });
    }
  }
  render(){
    const { address,height } = this.props.display
    return (
      <div className="itemSelect" style={{maxHeight:`${height-40}px`}}>
        <Form
          {...layout}
          name="basic"
          initialValues={{ remember: true }}
          onFinish={(e)=>this.onFinish(e)}
          onFinishFailed={onFinishFailed}
        >
          <Form.Item
            label="位置"
            name="address"
          >
            <Cascader options={address} onChange={onChange} changeOnSelect  style={{width: 280}} placeholder="请选择位置" />
          </Form.Item>
          <Form.Item
            label="建筑类型"
            name="architecturalType"
          >
            <Checkbox.Group options={architecturalType} onChange={onChange} />
          </Form.Item>
          
          
          
          <Form.Item
            label="绿建等级"
            name="gbes"
          >
            <Checkbox.Group options={gbes} defaultValue={['1']} onChange={onChange} />
          </Form.Item>
          <Form.Item
            label="节能标准"
            name="energySavingStandard"
          >
            <Checkbox.Group options={energySavingStandard} defaultValue={['1']} onChange={onChange} />
          </Form.Item>
          <Form.Item
            label="经过节能改造"
            name="energySavingTransformationOrNot"
          >
            <Checkbox.Group options={energySavingTransformationOrNot} defaultValue={['1']} onChange={onChange} />
          </Form.Item>
          <Form.Item
            label="供冷方式"
            name="coolingMode"
          >
            <Checkbox.Group options={coolingMode} defaultValue={['1']} onChange={onChange} />
          </Form.Item>
          <Form.Item
            label="供暖方式"
            name="heatingMode"
          >
            <Checkbox.Group options={heatingMode} defaultValue={['1']} onChange={onChange} />
          </Form.Item>
          <Form.Item
            label="可再生能源利用"
            name="whetherToUseRenewableResources"
          >
            <Checkbox.Group options={whetherToUseRenewableResources} defaultValue={['1']} onChange={onChange} />
          </Form.Item>
          <Form.Item
            label="层数"
            className="floor"
          >
            <Form.Item name="floor">
              <Checkbox.Group options={floor} onChange={(v)=>this.floorChange(v)} />
            </Form.Item>
            {this.state.other &&  <Input.Group compact >
              <Form.Item name="floorMin">
                <Input style={{ width: 100, textAlign: 'center' }}/> 
              </Form.Item>
              
              <Input
                className="site-input-split"
                style={{
                  width: 30,
                  borderLeft: 0,
                  borderRight: 0,
                  pointerEvents: 'none',
                }}
                placeholder="~"
                disabled
              />
              <Form.Item name="floorMax">
                <Input
                  className="site-input-right"
                  suffix="层"
                  style={{
                    width: 150,
                    textAlign: 'center',
                  }}
                />
              </Form.Item>
              
            </Input.Group>}
          </Form.Item>
          <Form.Item
            label="建成时间"
            name="date"
          >
            <RangePicker picker="year" format="YYYY" style={{width:'278px'}}/>
          </Form.Item>
          <Form.Item
            label="建筑面积"
          >
            <Input.Group compact>
              <Form.Item name="areaMin">
                <Input style={{ width: 100, textAlign: 'center' }} placeholder="最小值" />
              </Form.Item>
                <Input 
                className="site-input-split"
                style={{
                  width: 30,
                  borderLeft: 0,
                  borderRight: 0,
                  pointerEvents: 'none',
                }}
                placeholder="~"
                disabled
              />
              <Form.Item name="areaMax">
                <Input
                  className="site-input-right"
                  style={{
                    width: 150,
                    textAlign: 'center',
                  }}
                  placeholder="最大值"
                  suffix="㎡"
                />
              </Form.Item>
              
            </Input.Group>
          </Form.Item>
          
          <Form.Item
            label="单位面积电耗"
          >
            <Input.Group compact>
              <Form.Item name="electricMin">
                <Input style={{ width: 100, textAlign: 'center' }} placeholder="最小值" />
              </Form.Item>
              <Input
                className="site-input-split"
                style={{
                  width: 30,
                  borderLeft: 0,
                  borderRight: 0,
                  pointerEvents: 'none',
                }}
                placeholder="~"
                disabled
              />
              <Form.Item name="electricMax">
                <Input
                  className="site-input-right"
                  style={{
                    width: 150,
                    textAlign: 'center',
                  }}
                  placeholder="最大值"
                  suffix="kWh/㎡"
                />
              </Form.Item>
              
            </Input.Group>
          </Form.Item>
          <Form.Item
            label="单位面积汽耗"
          >
            <Input.Group compact>
              <Form.Item name="gasMin">
                <Input style={{ width: 100, textAlign: 'center' }} placeholder="最小值" />  
              </Form.Item>
              <Input
                className="site-input-split"
                style={{
                  width: 30,
                  borderLeft: 0,
                  borderRight: 0,
                  pointerEvents: 'none',
                }}
                placeholder="~"
                disabled
              />
              <Form.Item name="gasMax">
                <Input
                  className="site-input-right"
                  style={{
                    width: 150,
                    textAlign: 'center',
                  }}
                  placeholder="最大值"
                  suffix="m³/㎡"
                />
              </Form.Item>
              
            </Input.Group>
          </Form.Item>
          <Form.Item
            label="单位面积水耗"
            name="water"
          >
            <Input.Group compact>
              <Form.Item name="waterMin">
                <Input style={{ width: 100, textAlign: 'center' }} placeholder="最小值" />
              </Form.Item>
              
              <Input
                className="site-input-split"
                style={{
                  width: 30,
                  borderLeft: 0,
                  borderRight: 0,
                  pointerEvents: 'none',
                }}
                placeholder="~"
                disabled
              />
              <Form.Item name="waterMax">
                <Input
                  className="site-input-right"
                  style={{
                    width: 150,
                    textAlign: 'center',
                  }}
                  placeholder="最大值"
                  suffix="m³/㎡"
                />
              </Form.Item>
              
            </Input.Group>
          </Form.Item>
          <Form.Item wrapperCol={{ span: 12, offset: 6 }} style={{textAlign:'center'}}>
            <Button type="primary" htmlType="submit">
              筛选
            </Button>
          </Form.Item>
        </Form>
        
        
      </div>
    );
  }
} 

export default connect(({ display, loading }) => ({
  display,
  loading: loading.models.display,
}))(ItemSelect);