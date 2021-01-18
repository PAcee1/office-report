package cn.afterturn.easypoi.handler.inter;

import java.util.List;
import java.util.Map;

/**
 * @author jueyue on 18-2-2.
 * @version 3.0.4
 */
public interface IExcelDictHandler {

    /**
     * 返回字典所有值
     * key: dictKey
     * value: dictValue
     * @param dict  字典Key
     * @return
     */
    default public List<Map> getList(String dict) {
        return null;
    }

    /**
     * 从值翻译到名称
     *
     * @param dict  字典Key
     * @param obj   对象
     * @param name  属性名称
     * @param value 属性值
     * @return
     */
    public String toName(String dict, Object obj, String name, Object value);

    /**
     * 从名称翻译到值
     *
     * @param dict  字典Key
     * @param obj   对象
     * @param name  属性名称
     * @param value 属性值
     * @return
     */
    public String toValue(String dict, Object obj, String name, Object value);

}
