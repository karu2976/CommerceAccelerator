


--      @version $Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/src/main/sql/ddlgen/csa_content_mgmt_ddl.xml#1 $$Change: 1385662 $  

create table csa_media_content (
	asset_version	bigint	not null,
	id	varchar(40)	not null,
	link_url	varchar(255)	null,
	alt_text	varchar(255)	null,
	image_srcset	varchar(1056)	null,
	image_text_1	varchar(255)	null,
	image_text_2	varchar(255)	null,
	image_text_3	varchar(255)	null,
	image_text_4	varchar(255)	null
,constraint csa_media_content_p primary key (id,asset_version));


create table csa_media_content_xlate (
	asset_version	bigint	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	tinyint	not null,
	version_editable	tinyint	not null,
	pred_version	bigint	null,
	checkin_date	datetime	null,
	translation_id	varchar(40)	not null,
	name	nvarchar(255)	null,
	title	nvarchar(255)	null,
	description	nvarchar(255)	null,
	link_url	nvarchar(255)	null,
	alt_text	nvarchar(255)	null,
	image_text_1	nvarchar(255)	null,
	image_text_2	nvarchar(255)	null,
	image_text_3	nvarchar(255)	null,
	image_text_4	nvarchar(255)	null
,constraint csa_media_content_xlate_p primary key (translation_id,asset_version));

create index csa_media_cont_wsx on csa_media_content_xlate (workspace_id);
create index csa_media_cont_cix on csa_media_content_xlate (checkin_date);

create table csa_media_media_xlate (
	asset_version	bigint	not null,
	id	varchar(40)	not null,
	locale	varchar(40)	not null,
	translation_id	varchar(40)	not null
,constraint csa_media_media_xlate_p primary key (id,locale,asset_version));

commit;


