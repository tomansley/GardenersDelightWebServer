package com.gdelight.server.dao;

import java.util.Properties;

//import org.apache.log4j.Logger;

import com.gdelight.domain.base.BaseRequestBean;
import com.gdelight.server.service.PostServiceException;
import com.gdelight.tools.dao.BaseDAO;

public class LoginDAO extends BaseDAO {

	public static final String POST_REQUEST_LOGIN = "Login";

	//private static Logger log = Logger.getLogger(LoginPostRequestDAO.class);
	
	/**
	 * Method which sets all necessary fields into the database for later retrieval and data analysis.
	 * @param bean the bean holding the data being stored into the database.
	 * @throws PostServiceException
	 */
	public void addPostRequestResults(BaseRequestBean bean) throws PostServiceException {
		
		try {

			Properties properties = new Properties();
			
//			properties.put(FIELD_FK_REQUEST_ID, bean.getInternalId());
//			//we might have a CMVStarterRequestBean because something failed.  In which
//			//case just submit zero's for everything
//			properties.put(FIELD_TOTAL_ACTIVE_LENDERS, new Integer(0));
//			properties.put(FIELD_TOTAL_VALID_LENDERS, new Integer(0));
//			properties.put(FIELD_APPROVED_LENDER_ID, new Integer(0));
//			
//			//could be an instance of CMVStarterRequestBean in which case it failed at the very early stages.
//			if (bean instanceof CMVNewLoanBean) {
//				
//				CMVNewLoanBean data = (CMVNewLoanBean) bean;
//				
//				properties.put(FIELD_TOTAL_ACTIVE_LENDERS, ((CMVNewLoanMetricBean) data.getMetrics()).getActiveLendersCount());
//				properties.put(FIELD_TOTAL_VALID_LENDERS, ((CMVNewLoanMetricBean) data.getMetrics()).getValidLendersCount());
//				
//				if (data.getStatus().equals(STATUS_TYPE.ACCEPTED)) {
//					properties.put(FIELD_APPROVED_LENDER_ID, data.getApprovedLender().getId());
//					
//
//					
//					//////////OLD (pre 9/7) settings
//					//properties.put(FIELD_LENDER_COMMISSION, data.getApprovedLender().getPayout());
//					//properties.put(FIELD_CMV_REVENUE_SHARE, data.getApprovedLender().getPayout() - data.getRevenueShare());
//					
//					//LENDER PAYOUT = FIXED AMOUNT
//					if (data.getApprovedLender().getPayout() > 0.0) {
//						properties.put(FIELD_LENDER_COMMISSION, data.getApprovedLenderCommission());
//					//LENDER PAYOUT = VARIABLE
//					} else {
//						properties.put(FIELD_LENDER_COMMISSION, data.getApprovedLenderCommission());
//					}
//					properties.put(FIELD_CMV_REVENUE_SHARE, data.getApprovedCMVRevenueShare());							
//					properties.put(FIELD_LENDER_LOAN_GUID, data.getApprovedLenderLoanId());
//					
//					log.debug("************************* LOAN ACCEPTED - Payout Values Set ********************");
//					log.debug("***** Lender Commission: " + data.getApprovedLenderCommission());
//					log.debug("***** CMV Revenue Share: " + data.getApprovedCMVRevenueShare());
//					log.debug("********************************************************************************");
//				}
//			
//			}
			System.out.println(properties);
			this.addSimpleData(DatabaseNames.USER_PROFILE, properties);

		} catch (Exception e) {
			e.printStackTrace();
			throw new PostServiceException(e, "Failure while trying to write new loan request results.");
		}

	}
	
}


