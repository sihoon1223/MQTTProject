import React, { Component } from 'react';


class SmallChart extends Component {
  render() {
    const style = {
      //display:'block',
      background:'white',
      borderRadius:'25px',
      width:'30%',
      height:'300px',
      float:'left',
      padding:'2%',
      margin:'1%'
    };

    return (
      <div>
        <div id={this.props.name} style={style}></div>
      </div>
    );
  }
}

export default SmallChart;
