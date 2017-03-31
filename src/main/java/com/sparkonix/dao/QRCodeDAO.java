package com.sparkonix.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import com.sparkonix.entity.QRCode;

import io.dropwizard.hibernate.AbstractDAO;

public class QRCodeDAO extends AbstractDAO<QRCode> {

	public QRCodeDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public QRCode save(QRCode qrcode) throws Exception {
		return persist(qrcode);
	}

	public QRCode getByQRCode(String qrcode) throws Exception {
		return get(qrcode);
	}

	public List<QRCode> findAll() {
		// return list(namedQuery("com.sparkonix.entity.QRCode.findAll"));

		List<QRCode> qrCodeList;
		qrCodeList = list(namedQuery("com.sparkonix.entity.QRCode.findAll"));
		for (QRCode qrCode : qrCodeList) {
			initialize(qrCode.getCreatedBy());
		}
		return qrCodeList;
	}
}
