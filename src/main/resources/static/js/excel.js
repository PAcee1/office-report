// 表格ID,锁列数,宽度,高度
function FixTable(TableID, FixColumnNumber, width, height) {
    if ($("#" + TableID + "_tableLayout").length != 0) {
        $("#" + TableID + "_tableLayout").before($("#" + TableID));
        $("#" + TableID + "_tableLayout").empty();
    }
    else {
        $("#" + TableID).after("<div id='" + TableID + "_tableLayout' style='overflow:hidden;height:" + height + "px; width:" + width + "px;'></div>");
    }
    $('<div id="' + TableID + '_tableFix"></div>'
    + '<div id="' + TableID + '_tableHead"></div>'
    + '<div id="' + TableID + '_tableColumn"></div>'
    + '<div id="' + TableID + '_tableData"></div>').appendTo("#" + TableID + "_tableLayout");
    var oldtable = $("#" + TableID);
		var tableFixClone = oldtable.clone(true);
    tableFixClone.attr("id", TableID + "_tableFixClone");
    $("#" + TableID + "_tableFix").append(tableFixClone);
    var tableHeadClone = oldtable.clone(true);
    tableHeadClone.attr("id", TableID + "_tableHeadClone");
    $("#" + TableID + "_tableHead").append(tableHeadClone);
    var tableColumnClone = oldtable.clone(true);
    tableColumnClone.attr("id", TableID + "_tableColumnClone");
    $("#" + TableID + "_tableColumn").append(tableColumnClone);
    $("#" + TableID + "_tableData").append(oldtable);
    $("#" + TableID + "_tableLayout table").each(function () {
        $(this).css("margin", "0");
    });
    var HeadHeight = $("#" + TableID + "_tableHead thead").height();
    HeadHeight += 2;
    $("#" + TableID + "_tableHead").css("height", HeadHeight);
    $("#" + TableID + "_tableFix").css("height", HeadHeight);
    var ColumnsWidth = 0;
    var ColumnsNumber = 0;
    for (var i = 0; i < FixColumnNumber; i++) {
        var str = $("#" + TableID + "_tableData tr:last td:nth-child(" + (i + 1) + ")");
        ColumnsWidth += str.outerWidth(true);
        ColumnsNumber++;
    }
    ColumnsWidth += 2;
    $("#" + TableID + "_tableColumn").css("width", ColumnsWidth);
    $("#" + TableID + "_tableFix").css("width", ColumnsWidth);
    $("#" + TableID + "_tableData").scroll(function () {
        $("#" + TableID + "_tableHead").scrollLeft($("#" + TableID + "_tableData").scrollLeft());
        $("#" + TableID + "_tableColumn").scrollTop($("#" + TableID + "_tableData").scrollTop());
    });

    $("#" + TableID + "_tableFix").css({ "overflow": "hidden", "position": "relative", "z-index": "50"});
    $("#" + TableID + "_tableHead").css({ "overflow": "hidden", "width": width - 17, "position": "relative", "z-index": "45"});
    $("#" + TableID + "_tableColumn").css({ "overflow": "hidden", "height": height - 17, "position": "relative", "z-index": "40"});
    $("#" + TableID + "_tableData").css({ "overflow": "auto", "width": width, "height": height, "position": "relative", "z-index": "35" });
    if ($("#" + TableID + "_tableHead").width() > $("#" + TableID + "_tableFix table").width()) {
        $("#" + TableID + "_tableHead").css("width", $("#" + TableID + "_tableFix table").width());
        $("#" + TableID + "_tableData").css("height", height);
    }
    if ($("#" + TableID + "_tableColumn").height() > $("#" + TableID + "_tableColumn table").height()) {
        $("#" + TableID + "_tableColumn").css("height", $("#" + TableID + "_tableColumn table").height());
        $("#" + TableID + "_tableData").css("height", height);
    }
    $("#" + TableID + "_tableFix").offset($("#" + TableID + "_tableLayout").offset());
    $("#" + TableID + "_tableHead").offset($("#" + TableID + "_tableLayout").offset());
    $("#" + TableID + "_tableColumn").offset($("#" + TableID + "_tableLayout").offset());
    $("#" + TableID + "_tableData").offset($("#" + TableID + "_tableLayout").offset());

}

$(function(){
	var trIndex = 0;
	var tdIndex = 0;
	// 获取合并行数
	var rowspanNum = 1;
	// 获取合并列数
	var colspanNum = 1;
	
	// 固定行列
	FixTable('overflow', 1, ($(window).width()), ($(window).height()));
	// 加载完成移除动画
	parent.window.hideLoading();

    $(window).resize(function () {
		$('#overflow_tableLayout').css({
			'width': $(window).width() + 'px',
			'height': $(window).height() + 'px'
		})
		$('#overflow_tableData').css({
			'width': $(window).width() + 'px',
			'height': $(window).height() + 'px'
		})
		$('#overflow_tableHead').css({
			'width': $(window).width() - 17 + 'px',
		})
		$('#overflow_tableColumn').css({
			'height': $(window).height() - 17 + 'px'
		})
		$('#overflow_tableData').offset($('#overflow_tableLayout').offset());
	});
	
	
	$('#overflow td').hover(function(){
		tdIndex = 0;
		trIndex = 0;
		
		var this_td = $(this).parent('tr').find('td');
		// 当前行的列号
		var this_td_num = this_td.index($(this)); 
		var this_tr = $('#overflow tbody tr');
		// 当前行的行号
		var this_tr_num = $('#overflow tbody tr').index($(this).parent('tr'));
		
		// 获取第一行的列索引号
		var colNum = 1;
		for (var i = 0; i < this_td_num; i++) {
			colNum = this_td.eq(i).attr('colspan');
			if (typeof(colNum) == 'undefined') {
				colNum = 1;
			}
			tdIndex += parseInt(colNum);
		}
		// 判断和平行的现象
		var rowspan = 1;
		for (var i = 0; i < this_tr_num; i++) {
			for (var j = 0; j < tdIndex + 1; j++) {
				rowspan = this_tr.eq(i).find('td').eq(j).attr('rowspan');
				if (typeof(rowspan) == 'undefined') {
					continue;
				}
				if ((parseInt(rowspan) + i) == (this_tr_num + 1)) {
					tdIndex += 1;
				}
			}
		}
		trIndex = this_tr.index($(this).parent('tr'));
		
		rowspanNum = $(this).attr("rowspan");
		colspanNum = $(this).attr("colspan");
		if (typeof(rowspanNum) == 'undefined' || typeof(colspanNum) == 'undefined') {
			rowspanNum = 1;
			colspanNum = 1;
		}
		for (var i = 0; i < colspanNum; i++) {
			$('#overflow_tableHeadClone thead tr').find('th').eq(tdIndex+i).css({
				'background-color': '#FAF9D2',
				'color': '#42A642'
			});
		}
		for (var i = 0; i < rowspanNum; i++) {
			$('#overflow_tableColumnClone tbody tr').eq(trIndex+i).find('td').eq(0).css({
				'background-color': '#FAF9D2',
				'color': '#42A642'
			});
		}
	},function(){
		for (var i = 0; i < colspanNum; i++) {
			$('#overflow_tableHeadClone thead tr').find('th').eq(tdIndex+i).css({
				'background-color': '#E8E8E8',
				'color': '#777777'
			});
		}
		for (var i = 0; i < rowspanNum; i++) {
			$('#overflow_tableColumnClone tbody tr').eq(trIndex+i).find('td').eq(0).css({
				'background-color': '#E8E8E8',
				'color': '#777777'
			});
		}
	});
});



