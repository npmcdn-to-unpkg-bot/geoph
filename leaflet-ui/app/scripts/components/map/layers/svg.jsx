import {PropTypes} from 'react';
import {geoJson, latlng, marker, divIcon} from 'leaflet';
import {MapLayer} from 'react-leaflet';
import React from 'react';
import d3 from 'd3';
import { render, unmountComponentAtNode } from 'react-dom';

/**
 * @author Sebas
 */
 export default class D3Layer extends MapLayer {

   static propTypes = {
    higthligthStyleProvider: React.PropTypes.func,
  };


  constructor() {
    super();
  }

  componentWillMount() {
    super.componentWillMount();
    this.create()
  }


  componentDidUpdate(nextProps, nextState) {
    const {data, ...props} = this.props;
    this.update();
  }


  create(){
    this.leafletElement = geoJson();
    this.props.map._initPathRoot();
    this.svg= d3.select(this.props.map._container).select("svg"),
    this.g = this.svg.append("g").attr("class", "leaflet-zoom-hide");
    this.props.map.on('moveend', this.mapmove.bind(this));
    this.mapmove();
  }

  update(){
    //TODO:maybe a more efficent way can be implemented
    //clean
    this.mapmove();
  }

  renderPaths(data){
 
    var rateByCount = {};
    data.forEach(function(f) { rateByCount[f.properties.id] = +f.properties.projectCount;});
    let scales = {};
    scales.quantize = d3.scale.quantize().domain([0,0.2,0.4,0.5,1]).range(d3.range(9).map(function(i) { return "q" + i + "-9"; }));
    scales.radious = d3.scale.quantize().domain([[0,0.2,0.4,0.5,1]]).range(d3.range(9));
  
    var  map=this.props.map;
    // Use Leaflet to implement a D3 geometric transformation.
    function projectPoint(x, y) {
      var point = map.latLngToLayerPoint(new L.LatLng(y, x));
      this.stream.point(point.x, point.y);
    }

    var transform = d3.geo.transform({ point: projectPoint });
    var path = d3.geo.path().projection(transform);
    path.pointRadius((f)=>{
      return  6;
    });

    var points = this.g.selectAll("path").data(data);
    
    points.enter().append("path");
    points.exit().remove();

    points.attr("d", path)
    .on("click",this.onClick.bind(this));
    //.on("mouseover",this.onMouseover.bind(this))
    //.on("mouseout",this.onMouseout.bind(this));

    points.attr("class", function(d) {
      //return scales.quantize(rateByCount[d.properties.id]);
       const value=scales.quantize(rateByCount[d.properties.id]);
       console.log(value);
      return value ;
    })


  }

  onClick(properties){
    L.DomEvent._getEvent().stopPropagation();
    this.renderPopupContent(properties);
  }



  mapmove(e) {

    if (this.props.data && this.props.data.features){
      this.renderPaths(this.props.data.features);
    }else{
      console.log('Dataset is empty');
    }
  }



  renderPopupContent(feature) {
    let popup = L.popup()
    .setLatLng(L.latLng(feature.geometry.coordinates[1],feature.geometry.coordinates[0]))
    .openOn(this.props.map);


    if (this.props.children) {
      render(React.cloneElement(React.Children.only(this.props.children), feature.properties) ,popup._contentNode);
      popup._updateLayout();
      popup._updatePosition();
      popup._adjustPan();
    } 
  }



  render() {
    return this.renderChildrenWithProps({
      popupContainer: this.leafletElement,
    });
  }

}

