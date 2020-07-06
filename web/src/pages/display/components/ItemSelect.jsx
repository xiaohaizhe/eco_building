import React from 'react';
import { Cascader,Form, Input, Button, Checkbox,DatePicker  } from 'antd';
const { RangePicker } = DatePicker;

const options1 =  [
  {
      "children": [
          {
              "children": [
                  {
                      "children": [
                          {
                              "label": "古新路",
                              "value": "古新路"
                          },
                          {
                              "label": "春天大道",
                              "value": "春天大道"
                          }
                      ],
                      "label": "青浦区",
                      "value": "青浦区"
                  }
              ],
              "label": "上海市",
              "value": "上海市"
          }
      ],
      "label": "上海市",
      "value": "上海市"
  },]
const buildingType = [
  { label: '商场', value: '商场' },
  { label: '酒店', value: '酒店' },
  { label: '办公', value: '办公' },
  { label: '医院', value: '医院' },
  { label: '餐饮', value: '餐饮' },
  { label: '文化教育', value: '文化教育' },
  { label: '其他', value: '其他' }
];
const floor = [
  { label: '1~3层（低层）', value: '0' },
  { label: '4~6层（多层）', value: '1' },
  { label: '7层以上（高层）', value: '2' },
  { label: '其他', value: '3' }
];
const green = [
  { label: '0', value: '0' },
  { label: '1', value: '1' },
  { label: '2', value: '2' },
  { label: '3', value: '3' },
  { label: '未知', value: '未知' }
];
const standard = [
  { label: '不执行节能标准', value: '不执行节能标准' },
  { label: '50%', value: '50%' },
  { label: '65%', value: '65%' },
  { label: '75%以上', value: '75%以上' },
  { label: '未知', value: '未知' }
];
const isReform = [
  { label: '是', value: '是' },
  { label: '否', value: '否' },
  { label: '未知', value: '未知' }
];
const cooling = [
  { label: '集中供冷', value: '集中供冷' },
  { label: '分户供冷', value: '分户供冷' },
  { label: '无供冷', value: '无供冷' },
  { label: '未知', value: '未知' }
];
const heating = [
  { label: '集中供暖', value: '集中供暖' },
  { label: '分户采暖', value: '分户采暖' },
  { label: '无采暖', value: '无采暖' },
  { label: '未知', value: '未知' }
];
const renewable =[
  { label: '无', value: '无' },
  { label: '浅层地热能', value: '浅层地热能' },
  { label: '太阳能', value: '太阳能' },
  { label: '未知', value: '未知' }
];
const layout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 18 },
};
const tailLayout = {
  wrapperCol: { offset: 8, span: 16 },
};

const onFinish = values => {
  console.log('Success:', values);
}

const onFinishFailed = errorInfo => {
  console.log('Failed:', errorInfo);
};

function onChange(value) {
  console.log(value);
}
//层数变化
// const floorChange = value =>{
//   if(value.indexOf('3')){
//     this.setState({'other':true})
//   }
// }
class ItemSelect extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      other:false
    };
  
  }
  //层数变化
  floorChange(value){
    if(value.indexOf('3')>-1){
      this.setState({'other':true})
    }else{
      this.setState({'other':false})
    }
  }
  render(){
    return (
      <div className="itemSelect">
        <Form
          {...layout}
          name="basic"
          initialValues={{ remember: true }}
          onFinish={onFinish}
          onFinishFailed={onFinishFailed}
        >
          <Form.Item
            label="位置"
            name="username"
          >
            <Cascader options={options1} onChange={onChange} changeOnSelect  style={{width: 280}} placeholder="请选择位置" />
          </Form.Item>
          <Form.Item
            label="建筑类型"
            name="buildingType"
          >
            <Checkbox.Group options={buildingType} defaultValue={['Apple']} onChange={onChange} />
          </Form.Item>
          <Form.Item
            label="建筑面积"
            name="buildingArea"
          >
            <Input.Group compact>
              <Input style={{ width: 100, textAlign: 'center' }} placeholder="最小值" />
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
              <Input
                className="site-input-right"
                style={{
                  width: 150,
                  textAlign: 'center',
                }}
                placeholder="最大值"
                suffix="㎡"
              />
            </Input.Group>
          </Form.Item>
          <Form.Item
            label="建成时间"
            name="buildYear"
          >
            <RangePicker picker="year"/>
          </Form.Item>
          <Form.Item
            label="层数"
            name="floor"
          >
            <Checkbox.Group options={floor} defaultValue={['其他']} onChange={(v)=>this.floorChange(v)} />
            {this.state.other &&  <Input.Group compact >
              <Input style={{ width: 50, textAlign: 'center' }}/>
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
              <Input
                className="site-input-right"
                style={{
                  width: 50,
                  textAlign: 'center',
                }}
              />
            </Input.Group>}
          </Form.Item>
          <Form.Item
            label="绿建等级"
            name="green"
          >
            <Checkbox.Group options={green} defaultValue={['1']} onChange={onChange} />
          </Form.Item>
          <Form.Item
            label="节能标准"
            name="standard"
          >
            <Checkbox.Group options={standard} defaultValue={['1']} onChange={onChange} />
          </Form.Item>
          <Form.Item
            label="是否经过节能改造"
            name="isReform"
          >
            <Checkbox.Group options={isReform} defaultValue={['1']} onChange={onChange} />
          </Form.Item>
          <Form.Item
            label="供冷方式"
            name="cooling"
          >
            <Checkbox.Group options={isReform} defaultValue={['1']} onChange={onChange} />
          </Form.Item>
          <Form.Item
            label="供暖方式"
            name="heating"
          >
            <Checkbox.Group options={isReform} defaultValue={['1']} onChange={onChange} />
          </Form.Item>
          <Form.Item
            label="可再生能源利用"
            name="renewable "
          >
            <Checkbox.Group options={renewable} defaultValue={['1']} onChange={onChange} />
          </Form.Item>
          <Form.Item
            label="单位面积电耗"
            name="electric"
          >
            <Input.Group compact>
              <Input style={{ width: 100, textAlign: 'center' }} placeholder="最小值" />
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
              <Input
                className="site-input-right"
                style={{
                  width: 150,
                  textAlign: 'center',
                }}
                placeholder="最大值"
                suffix="kWh/㎡"
              />
            </Input.Group>
          </Form.Item>
          <Form.Item
            label="单位面积气耗"
            name="gas"
          >
            <Input.Group compact>
              <Input style={{ width: 100, textAlign: 'center' }} placeholder="最小值" />
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
              <Input
                className="site-input-right"
                style={{
                  width: 150,
                  textAlign: 'center',
                }}
                placeholder="最大值"
                suffix="m³/㎡"
              />
            </Input.Group>
          </Form.Item>
          <Form.Item
            label="单位面积水耗"
            name="water"
          >
            <Input.Group compact>
              <Input style={{ width: 100, textAlign: 'center' }} placeholder="最小值" />
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
              <Input
                className="site-input-right"
                style={{
                  width: 150,
                  textAlign: 'center',
                }}
                placeholder="最大值"
                suffix="m³/㎡"
              />
            </Input.Group>
          </Form.Item>
          <Form.Item wrapperCol={{ span: 12, offset: 6 }}>
            <Button type="primary" htmlType="submit">
              筛选
            </Button>
          </Form.Item>
        </Form>
        
        
      </div>
    );
  }
} 

export default ItemSelect;