


--  @version $Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/sql/ddlgen/csa_multisite_ddl.xml#1 $$Change: 1385662 $

create table csa_site_configuration (
	asset_version	numeric(19)	not null,
	id	varchar(40)	not null,
	resource_bundle	varchar(254)	null,
	large_site_icon	varchar(254)	null,
	default_country_code	varchar(2)	null,
	newpass_addr	varchar(254)	null,
	orderconfirm_addr	varchar(254)	null,
	changepass_addr	varchar(254)	null,
	registereduser_addr	varchar(254)	null
,constraint csa_site_config_p primary key (id,asset_version))


create table csa_site_languages (
	asset_version	numeric(19)	not null,
	id	varchar(40)	not null,
	languages	varchar(40)	not null,
	sequence_num	integer	not null
,constraint csa_site_lang_p primary key (id,sequence_num,asset_version))


create table csa_i18n_site_config (
	asset_version	numeric(19)	not null,
	id	varchar(40)	not null,
	default_lang	varchar(2)	null
,constraint csa_i18nsite_pconfig primary key (id,asset_version))



go
