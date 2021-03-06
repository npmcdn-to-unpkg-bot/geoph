 import geostats from  './geostats.js';
 import d3 from 'd3';

 export default  class JenksCssProvider{
 	constructor(values,breaks,classes){
 		this.geo=new geostats(values);
 		this.breaks = breaks;
 		this.generator = d3.scale.threshold().domain(this.getDomain(breaks)).range(this.getRange(breaks+2));  

 	}

 	getRange(size){
 		
		return d3.range(size).map(function(i) { return  i+"-9"; });
 	}

 	getDomain(breaks){
 		return this.geo.getClassJenks(this.breaks);
 	}

 	getCssClass(value){
 		let cssClass=this.generator(value);
 		if (!cssClass){
 			
 		}
 		return cssClass;
 	}

 }


