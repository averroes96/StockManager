/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Include;


/**
 *
 * @author med
 */
public interface Init {

    String DB_NAME = "jdbc:mysql://127.0.0.1/gdp";
    
    String ENCODING = "?useUnicode=yes&characterEncoding=UTF-8";
 
    String DB_NAME_WITH_ENCODING = DB_NAME + ENCODING;
 
    String USER = "root";
 
    String PASSWORD = "";
    
    String UPLOADED_FILE_PATH = "C:/gdp-uploads/";
    
    String CONNECTION_ERROR = "خطأ خلال محاولة الإتصال بقاعدة البيانات";
    
    String CONNECTION_ERROR_MESSAGE = "تعذر الإتصال بقاعدة البيانات، تأكد من أن السرفر يعمل وحاول مجددا";
    
    String SELL_ADDED = "تم إضافة البيع" ;
    
    String SELL_ADDED_MESSAGE = "تم إضافة البيع إلى قاعدة البيانات بنجاح" ;
    
    String UNKNOWN_ERROR = "خطأ تقني";
    
    String SELL_DELETED = "تم حذف البيع";
    
    String SELL_DELETED_MESSAGE = "تم حذف البيع بنجاح من قاعدة البيانات" ;
    
    String SELL_UPDATED = "تعديل البيع" ;
    
    String SELL_UPDATED_MSG = "تم تعديل هذا البيع بنجاح" ;
    
    String EMPTY_STOCK = "صفر كمية";
    
    String EMPTY_STOCK_MESSAGE = "تحقق من الكمية المحددة وما إذا كانت الكمية قد نفذت";
    
    String USER_INFO = "بيانات المستخدم خاطئة";
    
    String USER_INFO_MESSAGE = "كملة المرور أو إسم المستخدم خاطئ.. تأكد من أن الحساب يمتلك صلاحيات الولوج للتطبيق بطلب من الأدمن" ;

    String JR_ERROR = "خطأ مطبعي" ;
    
    String NO_PRODUCTS_FOUND = "لا يوجد منتوجات";
    
    String NO_PRODUCTS_FOUND_MESSAGE = "لا يوجد منتوجات في قاعدة البيانات.. قم بإضافة منتوجات جديدة لإضافة بيع جديد";
    
    String QTE_ADDED = "تم إضافة الكمية";
    
    String QTE_ADDED_MSG = "تم إضافة وتعديل كمية المنتج بنجاح";
    
    String INVALID_QTE = "كمية غير صالحة";
            
    String INVALID_QTE_MSG = "من فضلك قم بإدخال كمية منتوج صالحة";
    
    String INFO_MESSAGE = "تنبيه";
    
    String INFO_MSG = "قم بتحديد المنتوج الذي تريد تغيير صورته !" ;
    
    String INFO_MSG1 = "قم بتحديد المنتوج الذي تريد تعديل معلوماته !" ;
    
    String LOAD_IMAGE_ERROR = "فشل تحميل الصورة" ;
    
    String MISSING_FIELDS = "حقول إدخال غير مملوءة" ;
    
    String MISSING_FIELDS_MSG = "بعض حقول الإدخال الضرورية غير مملوءة" ;
    
    String INVALID_PRICE = "أسعار غير صالحة" ;
    
    String INVALID_PRICE_MSG = "من فضلك قم بإدخال سعر صالح" ;
    
    String PRODUCT_UPDATED = "تم تعديل المنتوج" ;
    
    String PRODUCT_UPDATED_MSG = "تم تعديل معلومات المنتوج بنجاح" ;
    
    String NO_IMAGE_FOUND = "لا يوجد صورة" ;
    
    String PRODUCT_DELETED = "تم حذف المنتوج";
    
    String PRODUCT_DELETED_MSG = "تم حذف المنتوج من قاعدة البيانات بنجاح" ;
    
    String EMPLOYER_DELETED = "تم حذف هذا المستخدم" ;

    String EMPLOYER_DELETED_MSG = "تم حذف هذا المستخدم من قاعدة البيانات بنجاح" ;
    
    String LAST_ADMIN = "اخرأدمن" ;
    
    String LAST_ADMIN_MSG = "لا يمكن حذف هذا الحساب لأنه حساب الأدمن الوحيد المتبقي" ;
    
    String UNVALID_NAME = "إسم غير صالح" ;
    
    String UNVALID_NAME_MSG = "من فضلك قم بإدخال إسم صالح";
    
    String USERNAME_ERROR = "إسم مستخدم غير صالح" ;
    
    String USERNAME_ERROR_MSG = "إسم المستخدم يجب أن يتكون من 5 حروف على الأقل و30 حرفا على الأكثر من دون فراغات.. ويجب أن يحتوي على حروف لاتينية فقط" ;
    
    String PASSWORD_ERROR = "كلمة المرور" ;
    
    String PASSWORD_ERROR_MSG_2 = "كلمة المرور غير متطابقة" ;
    
    String PASSWORD_ERROR_MSG = "كلمة المرور يجب أن تتكون من 7 حتى 30 حرفا على الأكثر" ;
    
    String PASSWORD_UPDATED = "كلمة مرور جديدة" ;
    
    String PASSWORD_UPDATED_MSG = "تم تعديل كلمة المرور بنجاح" ;
    
    String WRONG_PASSWORD = "كلمة مرور خاطئة" ;
    
    String WRONG_PASSWORD_MSG = "تأكد من صحة كلمة المرور التي قمت بإدخالها" ;
    
    String UNVALID_PHONE = "رقم هاتف غير صالح" ;
    
    String UNVALID_PHONE_MSG = "من فضلك قم بإدخال رقم هاتف صالح" ;
    
    String USER_ADDED = "إضافة المستخدم";
    
    String USER_ADDED_MSG = "تم إضافة هذا المستخدم بنجاح إلى قاعدة البيانات" ;
    
    String UPLOAD_IMAGE_FAILED = "حدث خطأ عند محاولة تحميل الصورة.. حاول مجددا من فضلك" ;
    
    String EMPLOYER_UPDATED = "تعديل الحساب" ;
    
    String EMPLOYER_UPDATED_MSG = "تم تعديل هذا الحساب بنجاح" ;
    
    String EMPLOYER_ASSIGNED = "إعادة دمج الحساب" ;
    
    String EMPLOYER_ASSIGNED_MSG = "تم إعادة دمج الحساب بنجاح" ;
    
    String BUY_DELETED = "حذف الشراء" ;
    
    String BUY_DELETED_MSG = "تم حذف هذا الشراء بنجاح من قاعدة البيانات" ;
    
    String LONG_NAME_ERROR = "إسم منتوج طويل" ;
    
    String LONG_NAME_ERROR_MSG = "إسم المنتوج لا يجب أن يتعدى 50 حرفا على الأكثر" ;
    
    String NOT_ENOUGH_QUANTITY = "كمية غير متوفرة" ;
    
    String NOT_ENOUGH_QUANTITY_MSG = "هذا المنتوج لا يتوفر على الكمية التي تم تحديدها" ;
    
    String ZERO_QTE = "كمية منعدمة" ;
    
    String ZERO_QTE_MSG = "المنتوج الذي قمت بتحديده منعدم الكمية" ;
    
    String LARGE_INTERVAL = "مجال كبير";
    
    String LARGE_INTERVAL_MSG = "أكبر مجال مسموح به هو 30 يوما.. يرجى تحديد مجال أصغر" ;
    
    String ILLEGAL_INTERVAL = "مجال بحث غير صالح";
    
    String ILLEGAL_INTERVAL_MSG = "تاريخ البداية يجب أن يكون أقل من أو يساوي تاريخ النهاية" ;

    
}
