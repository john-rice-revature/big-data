package csvreadwrite
import org.bson.types.ObjectId

case class Contact(_id: ObjectId, name: String, phone: String, group: String){}

object Contact {
    def apply(name: String, phone: String, group: String) : Contact = Contact(new ObjectId(), name, phone, group)

}
