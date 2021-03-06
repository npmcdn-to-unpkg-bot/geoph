import {getVisibles} from './layersUtil';
import translate from './translate';

const collect=(options)=>{
    let values=[];
    //first level iteration 
    options.forEach((item)=>{
        if (item.selected){ 
            values.push(item.id); //use values.push(item.name);  for debug purpose instead of id 
        }
        if (item.items){ //next levels iterations
            let nested=collect(item.items);
            values=values.concat(nested);
        }
    });
    return values;
}

const collectRange=(options)=>{
    return {'minSelected': options.minSelected, 'maxSelected': options.maxSelected};
}

export const collectValuesToSave = (state)=>{
    console.log('collectValuesToSave');
    let filters=state.filters.filterMain;
    let projectSearch=state.projectSearch;
    let map=state.map;
    let params={};
    let filterParams={};
    let selection;
    for(let param in filters){
        let options=filters[param].items;
        if (filters[param].isRange){
            selection=collectRange(filters[param]);
            if(selection.minSelected){              
                filterParams[param+'_min']=selection.minSelected;         
            }
            if(selection.maxSelected){              
                filterParams[param+'_max']=selection.maxSelected;         
            }
        } else {
            selection=collect(options);
            if(selection.length > 0){
                filterParams[param]=selection;            
            }
        }
    }
    ////console.log(params)
    if (projectSearch){
        let idsSelected = [];
        projectSearch.selected.map(it => idsSelected.push(it.id));
        Object.assign(filterParams, {'pr': idsSelected});     
    }
    Object.assign(params, {'filters': filterParams});
    if(map){ 
        let layers = getVisibles(map.get('layers')).toJS();
        let visibleLayers = [];
        layers.map((layer)=>{
            const {legends, name, keyName, id} = layer;
            let nameLabel =  keyName? translate("toolview.layers."+keyName) : name;
            visibleLayers.push({id, name: nameLabel, legends});
        })
        Object.assign(params, {'visibleLayers': visibleLayers});
    }
    Object.assign(params, {'map': map});
    return params;
}