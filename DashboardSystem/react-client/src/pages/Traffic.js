import React, { Component, Fragment } from 'react';
import { subscribe } from 'mqtt-react';
import { Col, Row, Container } from 'react-bootstrap';
import { AccumulationChart, ConnectionInfoChart, TopicPie, TopicChart } from '../trafficComponents';

//topic:'traffic/#'로 subscribe

class Traffic extends Component {
  constructor(props) {
    super(props);
    this.state = {
        topicChartData: "",
        accumChartData: "",
        connectionChartData: "",
        subData: "",
        receivingPieData: "",
        sendingPieData: "",
        accumulationPieData: "",
    }
  }

  componentDidMount() {
    const { mqtt } = this.props;
    console.log(mqtt);

    mqtt.publish('topic', 'traffic did mount');

    console.log("traffic did mount");
    console.log("connected : " + mqtt.connected);

    mqtt.on('message', function(topic, message){
        //console.log(topic.toString() + ", " + message.toString());
        //let topicArray = topic.toString().split('/');

        if(topic.toString().split('/')[1] === 'msgtopic') {
            this.setState({
                topicChartData: message.toString()
            }, function () {
                //console.log(this.state.topicChartData);
            });
        }
        else if(topic.toString().split('/')[1] === 'accumulate') {
            this.setState({
                accumChartData: message.toString()
            }, function () {
               // console.log(this.state.accumChartData);
            });
        }
        else if(topic.toString().split('/')[1] === 'subscription') {
            this.setState({
                subData: message.toString()
            }, function () {
               // console.log(this.state.subData);
            });
        }
        else if(topic.toString().split('/')[1] === 'connection') {
            this.setState({
                connectionChartData: message.toString()
            }, function () {
                //console.log(this.state.connectionChartData);
            });
        }
        else if(topic.toString().split('/')[1] === 'receivingPie') {
                this.setState({
                    receivingPieData: message.toString()
                }, function () {
                   // console.log(this.state.receivingPieData);
                });
            
        }
        else if(topic.toString().split('/')[1] === 'sendingPie') {
            this.setState({
                sendingPieData: message.toString()
            }, function () {
                //console.log(this.state.sendingPieData);
            });
        
        }
        else if(topic.toString().split('/')[1] === 'accumulationPie') {
            this.setState({
                accumulationPieData: message.toString()
            }, function () {
                //console.log(this.state.accumulationPieData);
            });
        
        }

    }.bind(this));
  }

  componentWillUnmount() {
    const { mqtt } = this.props;
    mqtt.publish('topic', 'unmount');

    console.log("traffic unmount");
  }

  componentWillReceiveProps(nextProps) {
    if (this.props.data !== nextProps.data) {
      //this.props.data.map((message, i) => i===0 && this.messageArrived(message));

      console.log("traffic receive props");
    }
  }

  

  render() {
    return (
      <Fragment>

        <Container fluid={true}>
        <Row className="justify-content-md-center">
        <Col>

        <TopicChart chartData={this.state.topicChartData}/>
        <AccumulationChart chartData={this.state.accumChartData}/>
        <ConnectionInfoChart chartData={this.state.connectionChartData}/>
        <TopicPie receivingData={this.state.receivingPieData} sendingData={this.state.sendingPieData}
            accumulationData={this.state.accumulationPieData}/>
        
        </Col>
        </Row>
        </Container>
      </Fragment>
    );
  }
}

export default subscribe({
  topic: 'traffic/#'
})(Traffic)
