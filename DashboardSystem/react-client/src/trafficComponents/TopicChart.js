import React, { Component, Fragment } from 'react';
import { Col, Container, Row } from 'react-bootstrap';
import makeChart from '../js/MakeChart';

//topic chart component

class TopicChart extends Component {
  chartData = [];

  componentDidMount() {
    console.log("topic chart did mount");

    //차트 생성, 초기화
    makeChart.create("msgtopic", "topicChartHolder", makeChart.topicLayoutStr, this.chartData);
  }

  componentWillUnmount() {
    console.log("topic chart unmount");
  }

  // "토픽개수;i번째토픽이름;i번째토픽의receiving;i번째토픽의sending; ... " 
  componentWillReceiveProps(nextProps) {
    if (this.props.chartData !== nextProps.chartData) {
      //this.props.chartData.map((message, i) => i===0 && this.messageArrived(message));
      
      console.log("receive props");
      this.messageArrived(this.props.chartData);  
    }
  }

  messageArrived(data) {
    let dataArray = data.split(';');

    let chartData =  [];
    for(let i=0; i<parseInt(dataArray[0], 10); i++) {
      chartData.push({"topic":dataArray[3*i + 1],"receiving":parseInt(dataArray[3*i + 2], 10), "sending":parseInt(dataArray[3*i + 3], 10)});
    }

    document.getElementById("msgtopic").setData(chartData);
  }

  render() {
    const style = {
      position:'absolute',
      width:'34.5vw',
      height: '42vh',
      top:'0vh',
      left:'1vw',
      background:'white',
      borderRadius: '25px',
      padding:'2%',
      margin:'1%',
      display:'inline-block',
      float:'left',
    };

    return (
      <Fragment>
        <Container fluid={true}>
        <Row className="justify-content-md-center">
        <Col>
        <div>
        <div style={style}  id="topicChartHolder"/>
        </div>

        </Col>
        </Row>
        </Container>
      </Fragment>
    );
  }
}

export default TopicChart;
