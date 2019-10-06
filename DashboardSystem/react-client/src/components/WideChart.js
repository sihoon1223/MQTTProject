import React, { Component } from 'react';

class WideChart extends Component {
  render() {
    const style = {
      //display:'inline-block',
      width:'100%',
      height:'500px',
      float:'left',
      padding:'2%'
    };

    return (
      <div>
        <div id={this.props.name} style={style}></div>
      </div>
    );
  }
}

export default WideChart;
