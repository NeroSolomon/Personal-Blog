# -*- coding: UTF-8 -*- 
from docx import Document

def convert_docx_to_html(docx_file, html_file):
    document = Document(docx_file)
    html = '<html><body>'
    for element in document.element.body:
        html += element.xml
    html += '</body></html>'
    with open(html_file, 'w', encoding='utf-8', errors='ignore') as file:
        file.write(html)

# 调用函数来将 mydoc.docx 转换为 mydoc.html
convert_docx_to_html('privacy_cn.docx', 'privacy_cn.html')