import React, { Component } from 'react';
import styled from 'styled-components';

/*const ChartWrapper = styled.div`
  width:'460px',
  height:'300px',
  background:'white',
  borderRadius: '25px',
  padding:'2%',
  margin:'1%',
  display:'inline-block',
  float:'left',
  position:'absolute',
  top:'${(props) => props.top}',
  left:'${(props) => props.left}'
  border: 1px solid black;
  &:hover {
    background: black;
  }
`;


const MidChart = ({children, name, ...rest}) => {
  return (
    <ChartWrapper {...rest} id={name}>
      {children}
      
    </ChartWrapper>
  );
};

export default MidChart;*/



class MidChart extends Component {
  render() {
    const style = {
      width:'460px',
      height:'300px',
      background:'white',
      borderRadius: '25px',
      padding:'2%',
      margin:'1%',
      display:'inline-block',
      float:'left',
    };

    return (
      <div>
        <div id={this.props.name} style={style}></div>
      </div>
    );
  }
}

export default MidChart;
