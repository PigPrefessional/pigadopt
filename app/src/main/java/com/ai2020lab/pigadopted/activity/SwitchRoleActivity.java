/*
 * Copyright (c) 2016. Justin Z All rights Reserved
 */

package com.ai2020lab.pigadopted.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ai2020lab.aiutils.common.LogUtils;
import com.ai2020lab.aiutils.common.ViewUtils;
import com.ai2020lab.pigadopted.R;
import com.ai2020lab.pigadopted.base.AIBaseActivity;
import com.ai2020lab.pigadopted.common.PrefManager;
import com.ai2020lab.pigadopted.model.user.Party;
import com.ai2020lab.pigadopted.model.user.RoleType;
import com.rey.material.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justin Z on 2016/4/17.
 * 502953057@qq.com
 */
public class SwitchRoleActivity extends AIBaseActivity {
	/**
	 * 日志标题
	 */
	private final static String TAG = SwitchRoleActivity.class.getSimpleName();

	/**
	 * 卖家下拉选择
	 */
	private Spinner providerSp;
	/**
	 * 买家下拉选择
	 */
	private Spinner customerSp;

	private List<Party> providerParties;
	private List<Party> customerParties;


	/**
	 * 入口
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		providerParties = getProviders();
		customerParties = getCustomers();
		setContentView(R.layout.activity_role_switch);
		setToolbar();
		assignViews();
		setProviderSp();
		setCustomerSp();
	}

	private void setToolbar() {
		supportToolbar(true);
		setToolbarTitle(getString(R.string.activity_switch_role));
		setToolbarLeft(R.drawable.toolbar_back_selector);
		setOnLeftClickListener(new OnLeftClickListener() {
			@Override
			public void onClick() {
				//TODO:返回登录界面前保存选择的partyID
				finish();
			}
		});
	}

	private void assignViews() {
		providerSp = (Spinner) findViewById(R.id.provider_sp);
		customerSp = (Spinner) findViewById(R.id.customer_sp);
	}

	/**
	 * 设置卖家Spinner
	 */
	private void setProviderSp() {
		final PartyAdapter partyAdapter = new PartyAdapter(this, providerParties);
		providerSp.setAdapter(partyAdapter);
		int partyID = PrefManager.getPartyIDForProvider(this);
		LogUtils.i(TAG, "保存的provider partyid-->" + partyID);
		partyAdapter.notifyDataSetChanged();
		providerSp.setSelection(getIndex(partyID, providerParties));
		providerSp.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			@Override
			public void onItemSelected(Spinner parent, View view, int position, long id) {
				Party party = partyAdapter.getItem(position);
				LogUtils.i(TAG, "选择的provider party-->" + party.toString());
				// 保存到SharedPreferences中
				PrefManager.setPartyIDForProvider(getActivity(), party.id);
			}
		});
	}

	/**
	 * 设置买家Spinner
	 */
	private void setCustomerSp() {
		final PartyAdapter partyAdapter = new PartyAdapter(this, customerParties);
		customerSp.setAdapter(partyAdapter);
		int partyID = PrefManager.getPartyIDForCustomer(this);
		LogUtils.i(TAG, "保存的customer partyid-->" + partyID);
		partyAdapter.notifyDataSetChanged();
		customerSp.setSelection(getIndex(partyID, customerParties));
		customerSp.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			@Override
			public void onItemSelected(Spinner parent, View view, int position, long id) {
				Party party = partyAdapter.getItem(position);
				LogUtils.i(TAG, "选择的customer party-->" + party.toString());
				// 保存到SharedPreferences中
				PrefManager.setPartyIDForCustomer(getActivity(), party.id);
			}
		});
	}

	class PartyAdapter extends BaseAdapter {

		private Context context;
		private List<Party> parties;

		PartyAdapter(Context context, List<Party> parties) {
			this.context = context;
			this.parties = parties;
		}

		@Override
		public int getCount() {
			return parties == null || parties.size() == 0 ? 0 : parties.size();
		}

		@Override
		public Party getItem(int position) {
			return parties == null || parties.size() == 0 ? null : parties.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = ViewUtils.makeView(context, R.layout.spinner_party_item,
						parent, false);
			}
			Party party = getItem(position);
			TextView textview = (TextView) convertView.findViewById(R.id.party_name_tv);
			textview.setText(party.name);
			return convertView;
		}
	}

	/**
	 * 组合卖家数据
	 */
	private List<Party> getProviders() {
		List<Party> providers = new ArrayList<>();
		Party party;
		party = new Party();
		party.id = 1;
		party.name = "正式卖家1";
		party.roleType = RoleType.PROVIDER;
		providers.add(party);
		party = new Party();
		party.id = 12;
		party.name = "测试卖家12";
		party.roleType = RoleType.PROVIDER;
		providers.add(party);
		party = new Party();
		party.id = 13;
		party.name = "测试卖家13";
		party.roleType = RoleType.PROVIDER;
		providers.add(party);
		return providers;
	}

	/**
	 * 组合买家数据
	 */
	private List<Party> getCustomers() {
		List<Party> customers = new ArrayList<>();
		Party party;
		party = new Party();
		party.id = 2;
		party.name = "正式买家2";
		party.roleType = RoleType.CUSTOMER;
		customers.add(party);
		party = new Party();
		party.id = 3;
		party.name = "正式买家3";
		party.roleType = RoleType.CUSTOMER;
		customers.add(party);
		party = new Party();
		party.id = 4;
		party.name = "正式买家4";
		party.roleType = RoleType.CUSTOMER;
		customers.add(party);
		party = new Party();
		party.id = 5;
		party.name = "正式买家5";
		party.roleType = RoleType.CUSTOMER;
		customers.add(party);
		party = new Party();
		party.id = 6;
		party.name = "正式买家6";
		party.roleType = RoleType.CUSTOMER;
		customers.add(party);
		party = new Party();
		party.id = 7;
		party.name = "正式买家7";
		party.roleType = RoleType.CUSTOMER;
		customers.add(party);
		party = new Party();
		party.id = 8;
		party.name = "正式买家8";
		party.roleType = RoleType.CUSTOMER;
		customers.add(party);
		party = new Party();
		party.id = 9;
		party.name = "正式买家9";
		party.roleType = RoleType.CUSTOMER;
		customers.add(party);
		party = new Party();
		party.id = 10;
		party.name = "正式买家10";
		party.roleType = RoleType.CUSTOMER;
		customers.add(party);
		party = new Party();
		party.id = 14;
		party.name = "测试买家14";
		party.roleType = RoleType.CUSTOMER;
		customers.add(party);
		return customers;
	}

	/**
	 * 根据partyID找到列表中的Party对象
	 */
	private Party getParty(int partyID, List<Party> parties) {
		for (Party party : parties) {
			if (party.id == partyID) {
				return party;
			}
		}
		return null;
	}

	/**
	 * 根据partyID找到Party对象在列表中的下标
	 */
	private int getIndex(int partyID, List<Party> parties) {
		LogUtils.i(TAG, "输入partyID-->" + partyID);
		int size = parties.size();
		int index = -1;
		for (int i = 0; i < size; i++) {
			if (parties.get(i).id == partyID) {
				index = i;
			}
		}
		LogUtils.i(TAG, "得到的位置为-->" + index);
		return index;
	}


}
