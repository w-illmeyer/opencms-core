(function(cms){
  
///// Content Handler Function definition //////////////////////////    
   
   /**
    * Displays the content of the preview item.
    * 
    * @param {Object} itemData server response as JSON object with preview data 
    */
   var showItemPreview = function(itemData) {                         
       //display the html preview
       $('.preview-area').append(itemData['previewdata']['itemhtml']);
       
       // display editable properties
       showEditArea(itemData['previewdata']['properties']);       
       
       //bind click event to preview close button 
       $('#cms-preview div.close-icon').click(function() {
          if ($(this).hasClass('cms-properties-changed')) {
              cms.galleries.loadSearchResults();
              $(this).removeClass('cms-properties-changed');
          }
          $(this).parent().fadeOut('slow');
          
       });   
       
       // fade in the preview      
       $('#cms-preview').fadeIn('slow');                    
   }   				            
   
   /**
    * Generates html for the editable properties and add to the dom.
    * 
    * @param {Object} itemProperties server response as JSON object with the editable properties
    */     
   var showEditArea = function(itemProperties) {
       // add button bar to the editable area
       var target = $('.edit-area').append('<span id="previewSave" class="cms-preview-button ui-state-default ui-corner-all">Save</span>\
                                           <span id="previewPublish" class="cms-preview-button ui-state-default ui-corner-all">Publish</span>');
       $('#previewSave').click(saveChangedProperty);
       $('#publishSave').click(publishChangedProperty);
              
       // generate editable form
       $.each(itemProperties, function() {
              $('<div style="margin: 2px;"></div>').attr('alt', this.name).appendTo(target)
                   .append('<span class="cms-item-title" style="margin-right:10px; width: 100px;">' + this.name + '</span>')
                   .append('<span class="cms-item-edit" style=" width: 100px;">' + this.value + '</span>');                                 
           });       
       
       // bind direct input to the editable fields
       $('.cms-item-edit').directInput({
                     marginHack: true,
                     live: false,
                     setValue: markChangedProperty
               });             
   }
   
   /**
    * Updates the value of the changed property in the preview and marks the element as changed.
    * This function overwrites 'setValue' method from the 'directInput' extension.
    *  
    * @param {Object} elem the html element to be changed
    * @param {Object} input the input field used to change the content of the html element
    */
   var markChangedProperty = function(elem, input) {      
      var previous = elem.text();
      var current = input.val();
      if (previous != current) {               
            elem.text(current);
            elem.addClass('cms-item-changed');         
      }
      elem.css('display', '');
      input.remove();          
   }
   
   /**
    * Refresh the preview after changes.
    * 
    * @param {Object} itemData the data to update the preview
    */     
   var refreshItemPreview = function (itemData) {
       $('#cms-preview div.preview-area, #cms-preview div.edit-area').empty();
       //display the html preview 
       $('.preview-area').append(itemData['previewdata']['itemhtml']);
       showEditArea(itemData['previewdata']['properties']);
         
   }
   
   /**
    * Callback function for click event on the 'save' button.
    */
   var saveChangedProperty = function() {
       var changedProperties = $('.cms-item-edit.cms-item-changed');
       
       // build json object with changed properties
       var changes = {
           'properties': []};
       $.each(changedProperties, function () {           
           var property = {};
           property['name'] =  $(this).closest('div').attr('alt');      
           property['value'] = $(this).text();
           changes['properties'].push(property);
       });

       // save changes via ajax if there are any
       if (changes['properties'].length != 0) {
          $.ajax({
             'url': vfsPathAjaxJsp,
             'data': {
                'action': 'setproperties',
                'data': JSON.stringify({
                   'path': $('#cms-preview').attr('alt'),
                   'properties': changes['properties']
                })
             },
             'type': 'POST',
             'dataType': 'json',
             'success': refreshItemPreview
          });
          $('#cms-preview div.close-icon').addClass('cms-properties-changed');
       }
       
   }
   
   /**
    * Callback function for click event on the 'publish' button.
    */
   var publishChangedProperty = cms.galleries.publishChangedProperty = function() {
       alert('Publish');
   } 
   
   var selectPath = function(item) {
       //alert($(item).closest('li').attr('alt'));       
   }

///// Default Content Handler ////////////////              
   /**
    * Default handler to display the preview for a resource. 
    * It can be used for all possible resource types. 
    */
   var defaultContentTypeHandler = cms.previewhandler.defaultContentTypeHandler = {
       'init': showItemPreview,
       'setValues': { 'widget': selectPath,
               'editor': 'test2'}    
   };
     
})(cms);