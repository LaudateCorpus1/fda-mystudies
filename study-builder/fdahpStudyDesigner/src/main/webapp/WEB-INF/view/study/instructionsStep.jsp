<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<style>
.modal-header .close { padding: 1rem 1rem; margin: -3rem -1rem -1rem auto; }
</style>

<!-- ============================================================== -->
<!-- Start right Content here -->
<!-- ============================================================== -->
<div class="col-sm-9.5 col-rc white-bg p-none">
  <!--  Start top tab section-->
  <form:form action="/studybuilder/adminStudies/saveOrUpdateInstructionStep.do?_S=${param._S}"
             name="basicInfoFormId"
             id="basicInfoFormId" method="post" data-toggle="validator" role="form">
    <div class="right-content-head">
      <div class="text-right">
        <div class="black-md-f dis-line pull-left line34">
          <span
              class="mr-xs cur-pointer"
              onclick="goToBackPage(this);"><img
              src="../images/icons/back-b.png" alt=""/></span>
          <c:if test="${actionTypeForQuestionPage == 'edit'}">Edit instruction step</c:if>
          <c:if test="${actionTypeForQuestionPage == 'view'}">View instruction step <c:set
              var="isLive">${_S}isLive</c:set>${not empty  sessionScope[isLive]?'<span class="eye-inc ml-sm vertical-align-text-top"></span>':''}
          </c:if>
          <c:if test="${actionTypeForQuestionPage == 'add'}">Add instruction step</c:if>
        </div>
        <div class="dis-line form-group mb-none mr-sm">
          <button type="button" class="btn btn-default gray-btn" onclick="goToBackPage(this);">
            Cancel
          </button>
        </div>
        <c:if test="${actionTypeForQuestionPage ne 'view'}">
          <div class="dis-line form-group mb-none mr-sm">
            <button type="button" class="btn btn-default gray-btn" id="saveId"
                    onclick="saveIns(this);">
              Save
            </button>
          </div>
          <div class="dis-line form-group mb-none">
            <button type="button" class="btn btn-primary blue-btn" id="doneId">Done</button>
          </div>
        </c:if>
      </div>
    </div>
    <!-- End top tab section-->
    <!-- Start body tab section -->
    <div class="right-content-body">
      <!-- form- input-->
      <input type="hidden" name="id" id="id" value="${instructionsBo.id}">
      <input type="hidden" name="questionnaireId" id="questionnaireId" value="${questionnaireId}">
      <input type="hidden" id="questionnaireShortId" value="${questionnaireBo.shortTitle}">
      <input type="hidden" id="type" name="type" value="complete"/>
      <input type="hidden" name="questionnairesStepsBo.stepId" id="stepId"
             value="${instructionsBo.questionnairesStepsBo.stepId}">
             <div class="row">
      <div class="col-md-6 pl-none mt-xlg">
        <div class="gray-xs-f mb-xs">Step short title or key (15 characters max)
          <span
              class="requiredStar">*
          </span>
          <span class="ml-xs sprites_v3 filled-tooltip"
                data-toggle="tooltip"
                title="A human readable step identifier and must be unique across all steps of the questionnaire. Note that this field cannot be edited once the study is Launched."></span>
        </div>
        <div class="form-group">
          <input autofocus="autofocus" type="text" custAttType="cust" class="form-control"
                 name="questionnairesStepsBo.stepShortTitle" id="shortTitleId"
                 value="${fn:escapeXml(instructionsBo.questionnairesStepsBo.stepShortTitle)}"
                 required="required" data-error="Please fill out this field"
                 maxlength="15" <c:if
              test="${not empty instructionsBo.questionnairesStepsBo.isShorTitleDuplicate && (instructionsBo.questionnairesStepsBo.isShorTitleDuplicate gt 0)}"> disabled</c:if>/>
          <div class="help-block with-errors red-txt"></div>
          <input type="hidden" id="preShortTitleId"
                 value="${fn:escapeXml(instructionsBo.questionnairesStepsBo.stepShortTitle)}"/>
        </div>
      </div>
      <div class="col-md-6 mt-xlg">
        <div class="gray-xs-f mb-xs">Step type</div>
        <div>Instruction step</div>
      </div>
    </div>
      <div class="clearfix"></div>
      <div class="gray-xs-f mb-xs">Title (250 characters max)
        <span class="requiredStar">*</span>
      </div>
      <div class="form-group">
        <input type="text" class="form-control" required data-error="Please fill out this field" name="instructionTitle"
               id="instructionTitle"
               value="${fn:escapeXml(instructionsBo.instructionTitle)}" maxlength="250"/>
        <div class="help-block with-errors red-txt"></div>
      </div>
      <div class="clearfix"></div>

      <div class="gray-xs-f mb-xs">Instruction text (500 characters max)
        <span class="requiredStar">*</span>
      </div>
      <div class="form-group">
        <textarea class="form-control" rows="5" id="summernote" name="instructionText"
                  required data-error="Please fill out this field"
                  maxlength="500">${instructionsBo.instructionText}</textarea>
        <div class="help-block with-errors red-txt"></div>
      </div>
      <div class="clearfix"></div>
      <c:if test="${questionnaireBo.branching}">
        <div class="col-md-4 col-lg-3 p-none">
          <div class="gray-xs-f mb-xs">Default destination step
            <span class="requiredStar">*</span>
            <span
                class="ml-xs sprites_v3 filled-tooltip"></span>
          </div>
          <div class="form-group">
            <select name="questionnairesStepsBo.destinationStep" id="destinationStepId"
                    data-error="Please choose one title" class="selectpicker" required data-error="Please fill out this field">
              <c:forEach items="${destinationStepList}" var="destinationStep">
                <option
                    value="${destinationStep.stepId}" ${instructionsBo.questionnairesStepsBo.destinationStep eq destinationStep.stepId ? 'selected' :''}>
                  Step ${destinationStep.sequenceNo} : ${destinationStep.stepShortTitle}</option>
              </c:forEach>
              <option
                  value="0" ${instructionsBo.questionnairesStepsBo.destinationStep eq "0" ? 'selected' :''}>
                Completion Step
              </option>
            </select>
            <div class="help-block with-errors red-txt"></div>
          </div>
        </div>
      </c:if>
    </div>
  </form:form>
  <!--  End body tab section -->
</div>
<!-- End right Content here -->
<script type="text/javascript">
  $(document).ready(function () {
	$('.studyClass').addClass("active");
    <c:if test="${actionTypeForQuestionPage == 'view'}">
    $('#basicInfoFormId input,textarea ').prop('disabled', true);
    $( '#summernote').summernote('disable');
    $('#basicInfoFormId select').addClass('linkDis');
    $('.selectpicker').selectpicker('refresh');
    </c:if>

    $(".menuNav li.active").removeClass('active');
    $(".sixthQuestionnaires").addClass('active');
    $("#shortTitleId").blur(function () {
      validateShortTitle('', function (val) {
      });
    });
    $("#summernote").blur(function () {
    	validatesummernote();
      });
  //summernote editor initialization
  var maxwords=500;  
    $('#summernote')
        .summernote(
            {
              placeholder: '',
              callbacks: {
                  onKeydown: function(e) {
                    var t = e.currentTarget.innerText;
                    if (t.length >= maxwords) {
                    if (e.keyCode != 8)
                      e.preventDefault();
                    }
                  },
                   onKeyup: function(e) {
                      var t = e.currentTarget.innerText;
                     if (t.length >= maxwords) {
                    if (e.keyCode != 8)
                      e.preventDefault();
                    }
                  },
                  onPaste: function(e) {
                	  var t = e.currentTarget.innerText;
                      var bufferText = ((e.originalEvent || e).clipboardData || 
                                         window.clipboardData).getData('Text');
                      e.preventDefault();
                      var all = t + bufferText;
                      var array = bufferText.slice(0, (maxwords-t.length))
                      document.execCommand('insertText', false, array);
                 
               	  }
     		  },
              tabsize: 2,
              height: 200,
              toolbar: [
                [
                  'font',
                  ['bold', 'italic']],
                [
                  'para',
                  ['paragraph',
                    'ul', 'ol']],
                ['font', ['underline']],
                ['insert', ['link']],
                ['hr'],
                ['clear'],
                ['cut'],
                ['undo'],
                ['redo'],
                ['fontname',
                  ['fontname']],
                ['fontsize',
                  ['fontsize']],],
                  fontSizes: ['8', '9', '10', '11', '12', '14','16', '18', '20', '22', '24', '36']

            });
    $('[data-toggle="tooltip"]').tooltip();
    $("#doneId").click(function () {
      //$("#doneId").attr("disabled", true);
      validateTitle();
      validatesummernote();
           validateShortTitle('', function (val) {
        if (val) {
          $('#shortTitleId').prop('disabled', false);
          if (isFromValid("#basicInfoFormId") && validatesummernote()) {
            document.basicInfoFormId.submit();
          } else {
            $("#doneId").attr("disabled", false);

          }
        } else {
          $("#doneId").attr("disabled", false);
        }
        
      });
    });
  });

  function saveIns() {
    $("body").addClass("loading");
    $("#saveId").attr("disabled", true);
    var valid = validatesummernote();
    var richTextVal = $('#summernote').val();
    if (null == richTextVal || richTextVal == '' || typeof richTextVal == 'undefined' || richTextVal == '<p><br></p>'){
    	valid = true;
 	       $('#summernote').attr('required', false);
 		   $('#summernote').parent().removeClass("has-danger").removeClass("has-error");
 		   $('#summernote').parent().find(".help-block").html("");
    }
    
    validateShortTitle('', function (val) {
      if (val && valid) {
        saveInstruction();
      } else {
        $("#saveId").attr("disabled", false);
        $("body").removeClass("loading");
      }
      
    });
  }
  
  function validatesummernote(){
	  var richTextVal = $('#summernote').val();
	  if (null != richTextVal && richTextVal != '' && typeof richTextVal != 'undefined' && richTextVal != '<p><br></p>'){
  	  var richText=$('#summernote').summernote('code');
  	  var escaped = $('#summernote').text(richText).html();
    	  $('#summernote').val(escaped);
     }
		 if ($('#summernote').summernote(
	     'code') === '<br>' || $('#summernote').summernote(
	     'code') === '' || $('#summernote').summernote('code') === '<p><br></p>') {
	   $('#summernote').attr(
	       'required', true);
	   $('#summernote')
	       .parent()
	       .addClass(
	           'has-error has-danger')
	       .find(".help-block")
	       .empty()
	       .append(
	           '<ul class="list-unstyled"><li>Please fill out this field</li></ul>');
	   return false;
	 } else {
	   $('#summernote').attr(
	       'required', false);
	   $('#summernote').parent()
	       .removeClass(
	           "has-danger")
	       .removeClass(
	           "has-error");
	   $('#summernote').parent().find(
	       ".help-block").html("");
	   return true;
	 }
	}
  
  function validateTitle(){
	  var titleValue = $('#instructionTitle').val();
      if (null == titleValue || titleValue == '' || typeof titleValue == 'undefined'){
    	  $('#instructionTitle')
	       .parent()
	       .find(".help-block")
	       .append(
	           '<ul class="list-unstyled"><li>Please fill out this field</li></ul>');
      }
  }
  function validateShortTitle(item, callback) {
    var shortTitle = $("#shortTitleId").val();
    var questionnaireId = $("#questionnaireId").val();
    var stepType = "Instruction";
    var thisAttr = $("#shortTitleId");
    var existedKey = $("#preShortTitleId").val();
    var questionnaireShortTitle = $("#questionnaireShortId").val();
    if (shortTitle != null && shortTitle != '' && typeof shortTitle != 'undefined') {
      if (existedKey != shortTitle) {
        $.ajax({
          url: "/studybuilder/adminStudies/validateQuestionnaireStepKey.do?_S=${param._S}",
          type: "POST",
          datatype: "json",
          data: {
            shortTitle: shortTitle,
            questionnaireId: questionnaireId,
            stepType: stepType,
            questionnaireShortTitle: questionnaireShortTitle
          },
          beforeSend: function (xhr, settings) {
            xhr.setRequestHeader("X-CSRF-TOKEN", "${_csrf.token}");
          },
          success: function getResponse(data) {
            var message = data.message;

            if ('SUCCESS' != message) {
              $(thisAttr).validator('validate');
              $(thisAttr).parent().removeClass("has-danger").removeClass("has-error");
              $(thisAttr).parent().find(".help-block").empty();
              callback(true);
            } else {
              $(thisAttr).val('');
              $(thisAttr).parent().addClass("has-danger").addClass("has-error");
              $(thisAttr).parent().find(".help-block").empty();
              $(thisAttr).parent().find(".help-block").append(
            	$("<ul><li> </li></ul>").attr("class","list-unstyled").text(
                  shortTitle
                  + " has already been used in the past"));
              callback(false);
            }
          },
          global: false
        });
      } else {
        callback(true);
      }
    } else {
      callback(false);
    }
  }

  function saveInstruction(item) {
    var instruction_id = $("#id").val();
    var questionnaire_id = $("#questionnaireId").val();
    var instruction_title = $("#instructionTitle").val();
    var instruction_text = $('#summernote').summernote('code');

    var shortTitle = $("#shortTitleId").val();
    var destinationStep = $("#destinationStepId").val();
    var step_id = $("#stepId").val();

    var instruction = new Object();
    if ((questionnaire_id != null && questionnaire_id != '' && typeof questionnaire_id
        != 'undefined') &&
        (shortTitle != null && shortTitle != '' && typeof shortTitle != 'undefined')) {
      instruction.questionnaireId = questionnaire_id;
      instruction.id = instruction_id;
      instruction.instructionTitle = instruction_title;
      instruction.instructionText = instruction_text;
      instruction.type = "save";

      var questionnaireStep = new Object();
      questionnaireStep.stepId = step_id;
      questionnaireStep.stepShortTitle = shortTitle;
      questionnaireStep.destinationStep = destinationStep
      instruction.questionnairesStepsBo = questionnaireStep;

      $('#basicInfoFormId').validator('destroy').validator();
      $('#instructionTitle').parent().find(".help-block").empty();
      $('#summernote').parent().find(".help-block").empty();
      var data = JSON.stringify(instruction);
      $.ajax({
        url: "/studybuilder/adminStudies/saveInstructionStep.do?_S=${param._S}",
        type: "POST",
        datatype: "json",
        data: {instructionsInfo: data},
        beforeSend: function (xhr, settings) {
          xhr.setRequestHeader("X-CSRF-TOKEN", "${_csrf.token}");
        },
        success: function (data) {
          var message = data.message;
          if (message == "SUCCESS") {
            $("#preShortTitleId").val(shortTitle);
            var instructionId = data.instructionId;
            var stepId = data.stepId;
            $("#id").val(instructionId);
            $("#stepId").val(stepId);
            $("#alertMsg").removeClass('e-box').addClass('s-box').text("Content saved as draft");
            $(item).prop('disabled', false);
            $("#saveId").attr("disabled", false);
            $('#alertMsg').show();
            if ($('.sixthQuestionnaires').find('span').hasClass(
                'sprites-icons-2 tick pull-right mt-xs')) {
              $('.sixthQuestionnaires').find('span').removeClass(
                  'sprites-icons-2 tick pull-right mt-xs');
            }
            $("body").removeClass("loading");
          } else {
            $("#alertMsg").removeClass('s-box').addClass('e-box').text("Something went Wrong");
            $('#alertMsg').show();
          }
          setTimeout(hideDisplayMessage, 5000);
        },
        error: function (xhr, status, error) {
          $(item).prop('disabled', false);
          $('#alertMsg').show();
          $("#alertMsg").removeClass('s-box').addClass('e-box').text("Something went Wrong");
          setTimeout(hideDisplayMessage, 5000);
        }
      });
    } else {
      $('#shortTitleId').validator('destroy').validator();
      if (!$('#shortTitleId')[0].checkValidity()) {
        $("#shortTitleId").parent().addClass('has-error has-danger').find(
            ".help-block").empty().append($("<ul><li> </li></ul>").attr("class","list-unstyled").text(
            "This is a required field"));
      }
    }
  }

  function goToBackPage(item) {
    $(item).prop('disabled', true);
    <c:if test="${actionTypeForQuestionPage ne 'view'}">
    bootbox.confirm({
      closeButton: true,
      message: 'You are about to leave the page and any unsaved changes will be lost. Are you sure you want to proceed?',
      buttons: {
        'cancel': {
          label: 'Cancel',
        },
        'confirm': {
          label: 'OK',
        },
      },
      callback: function (result) {
        if (result) {
          var a = document.createElement('a');
          a.href = "/studybuilder/adminStudies/viewQuestionnaire.do?_S=${param._S}";
          document.body.appendChild(a).click();
        } else {
          $(item).prop('disabled', false);
        }
      }
    });
    </c:if>
    <c:if test="${actionTypeForQuestionPage eq 'view'}">
    var a = document.createElement('a');
    a.href = "/studybuilder/adminStudies/viewQuestionnaire.do?_S=${param._S}";
    document.body.appendChild(a).click();
    </c:if>
  }

  $(document).on('mouseenter', '.dropdown-toggle',  function () {
      $(this).removeAttr("title");
  });
</script>