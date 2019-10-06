import React, { Component, Fragment } from 'react';
import { Table, Col, Container, Row } from 'react-bootstrap';
import {subscribe} from 'mqtt-react';
import Tree from 'react-d3-tree';

//topic:'subscription/#'

class Subscription extends Component {
  parents = [];
  sampleData = {name:'', children:[]};
  topicArray = [];
  data = [];
  topicKor = [];
  topicEng = [];

  constructor(props) {
      super(props)
      this.state = {
         topics: [],
         tableName: "",
         myTreeData: this.sampleData
      }
  }

  componentDidMount() {
    const { mqtt } = this.props;
    
    mqtt.publish('topic', 'subscription did mount');

    console.log("subscription did mount");
    console.log("connected : " + mqtt.connected);

    mqtt.on('message', function(topic, message){
      //console.log(topic.toString() + ", " + message.toString());

      if(topic.toString().split('/')[1] === 'tree') {
        this.treeMessageArrived(message.toString());
      }
      else if(topic.toString().split('/')[1] === 'table') {
        this.tableMessageArrived(message.toString());
      }
     

  }.bind(this));
  }

  componentWillUnmount() {
    const { mqtt } = this.props;
    mqtt.publish('topic', 'unmount');
    //mqtt.unsubscribe('subscription');

    console.log("unmount");
  }

  // subscription tab이 mount되면 전체 topic 받아오기
  // topic: "subscription/tree", message: "topics;topic;..."
  // topic tree 중 클릭된 노트의 subscription tableName
  // topic: "subcription/table", message: "table;client_id; ..."
  componentWillReceiveProps(nextProps) {
    if (this.props.data !== nextProps.data) {
      //this.props.data.map((message, i) => i===0 && this.messageArrived(message));

      console.log("receive props");
      //this.messageArrived(this.props.data);
    }
  }

  treeMessageArrived(data) {
    //console.log(data);

    let dataArray = data.split(';');
    let dataLength = parseInt(dataArray[0], 10);
    this.topicEng = [];
    this.topicKor = [];

    for(let i=0; i<dataLength; i++) {
      this.topicEng.push(dataArray[i + 1]);
     
    }
    for(let i=dataLength; i<dataLength*2; i++) {
      this.topicKor.push(dataArray[i + 1]);
    }
    console.log(dataLength);
    console.log(this.topicEng);
    console.log(this.topicKor);

    //if(dataArray[0] === 'topics') {
      //console.log(dataArray[0] + ", " + dataLength);

      for(let i=0; i<this.topicKor.length; i++) {
        //let topic = dataArray[i].split('/');
        let topic = this.topicKor[i].split('/');
        let length = topic.length;
        if(length > 2) {
          this.data.push({pName: topic[length-2], name: topic[length -1], depth: length-1, topic: this.topicKor[i]});
        }
        else {  //root
          this.data.push({pName: topic[length-1], name: topic[length -1], depth: length-1, topic: this.topicKor[i]});
        }
      }
      this.makeJSON(this.sampleData, this.data);
    //}
  }

  tableMessageArrived(data) {
    //console.log(data);

    let dataArray = data.split(';');
    let dataLength = dataArray.length;
    //console.log(dataLength);

    //if(dataArray[0] === 'table') {
      const temp = [];
      //console.log("length: " + dataLength);
      for(let j=0; j<dataLength - 1; j++) {
        temp.push({ id: (j+1), name: dataArray[j+1]});
      }
      this.setState({
        topics: temp
      });
      console.log(this.state.topics);
    //}
  }

  //make topic dataArray
  makeDataArray(data) {
    let dataArray = [];
    let temp = [];

    let sortedData = [];  //depth가 작은 순서대로 sort한 data
    let depth = 1;
    for(let i=0; i<data.length; i++) {
      for(let j=0; j<data.length; j++) {
        if(data[j].depth === depth)
          sortedData.push(data[j]);
      }
      depth++;
    }

    //dataArray = {parent:'자식이 있는 노드의 이름', data:[자식 노드들의 이름 배열]}
    for(let i=0; i<sortedData.length; i++) {
      let pName = sortedData[i].pName;
      let name = sortedData[i].name;
      //if(pName !== name) {
        for(let j=0; j<sortedData.length; j++) {
          if(pName === sortedData[j].pName && pName !== sortedData[j].name) {
            temp.push({name: sortedData[j].name});
          }
        }
        let flag = true;
        for(let j=0; j<dataArray.length; j++) {
          if(pName === dataArray[j].parent) {
            flag = false;
          }
        }
        if(flag)
          dataArray.push({parent: pName, data: temp});

        temp = [];
      //}
    }
    return dataArray;
  }

  getNodePath(nextData, parents, name) {
    let target = nextData.children[0];

    for(let i=parents.length -1; i>=0; i--) {
      if(target.children[parents[i]] === undefined)
        return "undefined";
      else
        target = target.children[parents[i]];
    }
    //console.log(target);
    return target;
  }

  getParents(dataArray, root, name) {
    for(let i=0; i<dataArray.length; i++) {
      for(let j=0; j<dataArray[i].data.length; j++) {
          if(dataArray[i].data[j].name === name) {
            this.parents.push(j);

            if(dataArray[i].parent !== root)
              this.getParents(dataArray, root, dataArray[i].parent);
          }
      }
    }
    return this.parents;
  }

  makeChildJSON(nextData, root, data) {
    let dataArray = this.makeDataArray(data);

    for(let j=0; j<dataArray.length; j++) {
      //console.log(temp[j]);
      console.log(dataArray[j]);
    }

    for(let i=0; i<dataArray.length; i++) {
      if(dataArray[i].parent === root) {
        let target = nextData.children[0].children;

        for(let k=0; k<dataArray[i].data.length; k++) {
          target.push({
            name: dataArray[i].data[k].name,
            children: []
          });
        }
      }
      else {
        let parents = this.getParents(dataArray, root, dataArray[i].parent);
        let target = this.getNodePath(nextData, parents, dataArray[i].parent);

        if(target !== "undefined") {
          for(let j=0; j<dataArray[i].data.length; j++) {
            target.children.push({
              name: dataArray[i].data[j].name,
              children: []
            });
          }
        }
        this.parents = [];
      }
    }
  }

  makeJSON(sampleData, data) {
    let target = sampleData.children;

    let root = "";
    for(let i=0; i<data.length; i++) {
      let pName = data[i].pName;
      let name = data[i].name;

      if(pName === name) {
        root = pName;
        target.push({
          name: name,
          children: []
        });
      }
    }
    this.makeChildJSON(sampleData, root, data);

    console.log(sampleData);
    this.setState({
      myTreeData: sampleData.children[0]
    });
  }

  renderTableData() { //동적으로 테이블 생성
    return this.state.topics.map((topic, index) => {
         return (
           <tr key={index}>
                <td>{topic.id}</td>
                <td>{topic.name}</td>
           </tr>
         )
    })
   }

  // Tree Node 클릭 시 topic: 'topic', message: 'sub;토픽명'으로 publish
  onClickTreeNode(nodeData, evt) {
    console.log(nodeData);
    const { mqtt } = this.props;
    let topicName = "";

    if(this.data[0].name === nodeData.name) {
      topicName = this.data[0].topic;
    }
    else {
      for(let i=1; i<this.data.length; i++) {
        if(this.data[i].name === nodeData.name && this.data[i].pName === nodeData.parent.name) {
          //console.log(this.data[i].topic);
          topicName = this.data[i].topic;
        }
      }
    }
    this.setState({tableName: topicName});
    
    for(let i=0; i<this.topicKor.length; i++) {
      if(topicName === this.topicKor[i]) {
        topicName = this.topicEng[i];
      }
    }

    mqtt.publish('topic', "sub;" + topicName);
  }

  setTableName() {
      const style2 = {
        textAlign:'center',
        height:'30px',
        //padding:'5px',
        fontWeight:'bold',
        fontSize:'1.1rem',
        //display:'inline-block',
        background: 'white',
        borderRadius: '25px',
        marginLeft: '100px',
        marginRight: '100px',
        //padding:'2%',
        margin: '1%'
      }
      return (<div style={style2}>{this.state.tableName}</div>)
    }

  render() {
    const treeStyle = {
      display:'inline-block',
      width:'65%',
      background: 'white',
      borderRadius: '25px',
      //height:'400px',
      height: 'calc(100vh - 160px)',
      float:'left',
      overflow:'auto',
      padding:'2%',
      margin:'1%',
      //marginTop:'5px',
    };

    const tableStyle = {
      display:'inline-block',
      width:'31%',
      //height:'500px',
      height: 'calc(100vh - 160px)',
      background: 'white',
      borderRadius: '25px',
      float:'left',
      overflow:'auto',
      //overflowX:'hidden',
      padding:'2%',
      margin:'1%',
      //marginTop:'5px',
      fontSize:'0.9rem'

    };

    return (
      <Fragment>
        <Container fluid={true}>
        <Row className="justify-content-md-center">
        <Col>

        {this.setTableName()}
        <div id="treeWrapper" style={treeStyle}>
          <Tree data={this.state.myTreeData} translate={{x:330, y:90}} shouldCollapseNeighborNodes={false} orientation="vertical" pathFunc="elbow" nodeSize={{x:140, y:140}} onClick={this.onClickTreeNode.bind(this)} />
        </div>

        <div style={tableStyle}>

          <Table striped bordered hover size="sm">
            <thead>
              <tr>
                <th>#</th>
                <th>Client ID</th>
              </tr>
            </thead>
            <tbody>
              {this.renderTableData()}
            </tbody>
          </Table>
        </div>

        </Col>
        </Row>
        </Container>
      </Fragment>
    );
  }
}

export default subscribe({
  topic: 'subscription/#'
})(Subscription)
