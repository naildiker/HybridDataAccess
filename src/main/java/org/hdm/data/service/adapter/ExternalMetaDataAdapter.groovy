import org.hdm.core.data.service.IHDMConnectInfo
import org.hdm.core.data.service.IHDMMetaDataAdapter
import org.hdm.core.objects.Entity

public class ExternalMetaDataAdapter implements  IHDMMetaDataAdapter
{

    Boolean connect(IHDMConnectInfo connectionInfo) {
        return null
    }

    List<Entity> getEntities() {
        return null
    }

    List<String> getAttributeNames() {
        return null
    }

    List<String> getAttributeNames(String entityName) {
        return null
    }

    List<String> getRelationshipNames() {
        return null
    }

    void close() {

    }
}