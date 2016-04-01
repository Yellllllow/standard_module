Ext.define('Ext.ux.KindEditor', {
	extend : 'Ext.Component',
	alias : 'widget.kindeditor',
	/**
	 * 配置编辑器的工具栏，其中”/”表示换行，”|”表示分隔符。
	 * 
	 * @type Array
	 */
	items : ['source', '|', 'undo', 'redo', '|', 'preview', 'print',
			'template', 'code', 'cut', 'copy', 'paste', 'plainpaste',
			'wordpaste', '|', 'justifyleft', 'justifycenter', 'justifyright',
			'justifyfull', 'insertorderedlist', 'insertunorderedlist',
			'indent', 'outdent', 'subscript', 'superscript', 'clearhtml',
			'quickformat', 'selectall', '|', 'fullscreen', '/', 'formatblock',
			'fontname', 'fontsize', '|', 'forecolor', 'hilitecolor', 'bold',
			'italic', 'underline', 'strikethrough', 'lineheight',
			'removeformat', '|', 'image', 'multiimage', 'flash', 'media',
			'insertfile', 'table', 'hr', 'emoticons', 'baidumap', 'pagebreak',
			'anchor', 'link', 'unlink'],

	/**
	 * designMode 为false时，要保留的工具栏图标。
	 * 
	 * @type Array
	 */
	noDisableItems : ['source', 'fullscreen'],

	/**
	 * true时根据 htmlTags 过滤HTML代码，false时允许输入任何代码。
	 * 
	 * @type Boolean
	 */
	filterMode : true,

	/**
	 * 指定要保留的HTML标记和属性。Object的key为HTML标签名，value为HTML属性数组，”.”开始的属性表示style属性。
	 * 
	 * @type Object
	 */
	htmlTags : {
		font : ['color', 'size', 'face', '.background-color'],
		span : ['.color', '.background-color', '.font-size', '.font-family',
				'.background', '.font-weight', '.font-style',
				'.text-decoration', '.vertical-align', '.line-height'],
		div : ['align', '.border', '.margin', '.padding', '.text-align',
				'.color', '.background-color', '.font-size', '.font-family',
				'.font-weight', '.background', '.font-style',
				'.text-decoration', '.vertical-align', '.margin-left'],
		table : ['border', 'cellspacing', 'cellpadding', 'width', 'height',
				'align', 'bordercolor', '.padding', '.margin', '.border',
				'bgcolor', '.text-align', '.color', '.background-color',
				'.font-size', '.font-family', '.font-weight', '.font-style',
				'.text-decoration', '.background', '.width', '.height',
				'.border-collapse'],
		'td,th' : ['align', 'valign', 'width', 'height', 'colspan', 'rowspan',
				'bgcolor', '.text-align', '.color', '.background-color',
				'.font-size', '.font-family', '.font-weight', '.font-style',
				'.text-decoration', '.vertical-align', '.background', '.border'],
		a : ['href', 'target', 'name'],
		embed : ['src', 'width', 'height', 'type', 'loop', 'autostart',
				'quality', '.width', '.height', 'align', 'allowscriptaccess'],
		img : ['src', 'width', 'height', 'border', 'alt', 'title', 'align',
				'.width', '.height', '.border'],
		'p,ol,ul,li,blockquote,h1,h2,h3,h4,h5,h6' : ['align', '.text-align',
				'.color', '.background-color', '.font-size', '.font-family',
				'.background', '.font-weight', '.font-style',
				'.text-decoration', '.vertical-align', '.text-indent',
				'.margin-left'],
		pre : ['class'],
		hr : ['class', '.page-break-after'],
		'br,tbody,tr,strong,b,sub,sup,em,i,u,strike,s,del' : []
	},

	/**
	 * true时美化HTML数据。
	 * 
	 * @type Boolean
	 */
	wellFormatMode : true,

	/**
	 * 2或1或0，2时可以拖动改变宽度和高度，1时只能改变高度，0时不能拖动。
	 * 
	 * @type Number
	 */
	resizeType : 0,

	/**
	 * 可视化模式或代码模式
	 * 
	 * @type Boolean
	 */
	designMode : true,

	/**
	 * 加载编辑器后变成全屏模式。
	 * 
	 * @type Boolean
	 */
	fullscreenMode : false,
	uploadJson : '',

	themeType : 'default',
	allowPreviewEmoticons : false,
	allowImageUpload : false,
	extraFileUploadParams:{},

	initComponent : function() {
		this.html = "<textarea id='" + this.getId() + "-input' name='"
				+ this.getId() + "'></textarea>";
		this.callParent(arguments);
		//console.log(this.id);
		this.on("boxready", function(t) {
					this.inputEL = Ext.get(this.getId() + "-input");
					this.editor = KindEditor.create('textarea[name="'
									+ this.getId() + '"]', {
								width : t.getWidth() + 4,
								height : t.getHeight() - 4,
								allowPreviewEmoticons : this.allowPreviewEmoticons,
								allowImageUpload : this.allowImageUpload,
								items : this.items,
								noDisableItems : this.noDisableItems,
								filterMode : this.filterMode,
								htmlTags : this.htmlTags,
								wellFormatMode : this.wellFormatMode,
								resizeType : this.resizeType,
								themeType : this.themeType,
								designMode : this.designMode,
								fullscreenMode : this.fullscreenMode,
								afterCreate : this.afterCreate,
								afterChange : this.afterChange,
								afterTab : this.afterTab,
								afterFocus : this.afterFocus,
								afterBlur : this.afterBlur,
								afterUpload : this.afterUpload,
								uploadJson: this.uploadJson,
								filePostName:this.filePostName,
								extraFileUploadParams:this.extraFileUploadParams
							});
					this.editor.readonly(this.readonly);
				});
		this.on("resize", function(t, w, h) {
					this.editor.resize(w + 4, h - 4)

				});
	},
	setValue : function(value) {
		if (this.editor) {
			this.editor.html(value);
		}
	},
	reset : function() {
		if (this.editor) {
			this.editor.html('');
		}
	},
	setRawValue : function(value) {
		if (this.editor) {
			this.editor.text(value);
		}
	},
	getValue : function() {
		if (this.editor) {
			return this.editor.html();
		} else {
			return ''
		}
	},
	getRawValue : function() {
		if (this.editor) {
			return this.editor.text();
		} else {
			return ''
		}
	}
});