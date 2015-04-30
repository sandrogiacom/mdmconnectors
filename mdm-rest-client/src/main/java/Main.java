import com.totvslabs.mdm.restclient.MDMRestConnection;
import com.totvslabs.mdm.restclient.MDMRestConnectionFactory;
import com.totvslabs.mdm.restclient.command.CommandListDatasource;
import com.totvslabs.mdm.restclient.command.CommandListEntity;
import com.totvslabs.mdm.restclient.command.CommandListTenant;
import com.totvslabs.mdm.restclient.command.CommandPostStaging;
import com.totvslabs.mdm.restclient.vo.EnvelopeVO;
import com.totvslabs.mdm.restclient.vo.GenericVO;


public class Main {

	public static void main(String[] args) {
		MDMRestConnection connection = MDMRestConnectionFactory.getConnection("https://app.fluigdata.com:443/mdm/");

		CommandListTenant tenantCommand = new CommandListTenant();

		EnvelopeVO envelope = connection.executeCommand(tenantCommand);

		for (GenericVO vo : envelope.getHits()) {
			System.out.println(vo);

			CommandListDatasource ds = new CommandListDatasource(((GenericVO) vo).get_mdmId());
			EnvelopeVO dsResult = connection.executeCommand(ds);

			for (GenericVO string : dsResult.getHits()) {
				System.out.println(string);

				CommandListEntity entity = new CommandListEntity(((GenericVO) string).get_mdmTenantId(), ((GenericVO) string).get_mdmDataSourceId());
				EnvelopeVO entityCommand = connection.executeCommand(entity);

				for (GenericVO entityPOJO : entityCommand.getHits()) {
					System.out.println(entityPOJO);
				}



				CommandPostStaging staging = new CommandPostStaging(((GenericVO) string).get_mdmTenantId(), ((GenericVO) string).get_mdmDataSourceId(),"test", dsResult.getHits());
				EnvelopeVO executeCommand = connection.executeCommand(staging);
				System.out.println(executeCommand);
			}
		}
	}

}
