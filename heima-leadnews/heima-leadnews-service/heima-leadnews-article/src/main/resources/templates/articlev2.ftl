<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title!''}</title>

    <style>
        body {
            margin: 0;
            padding: 16px;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial;
            background: #fafafa;
            color: #333;
        }
        .page-title {
            font-size: 26px;
            font-weight: bold;
            line-height: 1.4;
            margin-bottom: 10px;
        }
        .info-line {
            font-size: 14px;
            color: #888;
            margin-bottom: 20px;
        }
        .content-text {
            font-size: 18px;
            line-height: 1.8;
            margin: 16px 0;
            white-space: pre-wrap;
        }
        .content-image {
            width: 100%;
            margin: 12px 0;
            border-radius: 6px;
        }
    </style>
</head>
<body>

<!-- 标题 -->
<div class="page-title">${title!''}</div>

<!-- 作者 / 发布时间 / 创建时间 -->
<div class="info-line">
    作者：${author!''}
    &nbsp;&nbsp;|&nbsp;&nbsp; 发布时间：${publishTime!''}
    &nbsp;&nbsp;|&nbsp;&nbsp; 创建时间：${createdTime!''}
</div>

<!-- 图文内容 -->
<#if content??>
    <#list content as item>
        <#if item.type == "text">
            <div class="content-text">${item.value?html}</div>
        <#elseif item.type == "image">
            <img class="content-image" src="${item.value}" alt="image"/>
        </#if>
    </#list>
</#if>

</body>
</html>
